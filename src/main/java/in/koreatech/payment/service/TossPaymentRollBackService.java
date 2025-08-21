package in.koreatech.payment.service;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_ROLLBACK;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import in.koreatech.koin.domain.order.cart.repository.CartRepository;
import in.koreatech.koin.domain.order.model.Order;
import in.koreatech.koin.domain.order.model.Payment;
import in.koreatech.koin.domain.order.model.PaymentCancel;
import in.koreatech.koin.domain.order.model.PaymentIdempotencyKey;
import in.koreatech.koin.domain.order.repository.OrderRepository;
import in.koreatech.koin.domain.order.repository.PaymentCancelRepository;
import in.koreatech.koin.domain.order.repository.PaymentIdempotencyKeyRepository;
import in.koreatech.koin.domain.order.repository.PaymentRepository;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.order.shop.repository.OrderableShopRepository;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.koin.domain.user.repository.UserRepository;
import in.koreatech.payment.client.TossPaymentClient;
import in.koreatech.payment.client.dto.response.PaymentCancelResponse;
import in.koreatech.payment.event.TossPaymentRollBackEvent;
import in.koreatech.payment.model.redis.TemporaryPayment;
import in.koreatech.payment.repository.redis.TemporaryPaymentRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentRollBackService implements PaymentRollBackService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentIdempotencyKeyRepository paymentIdempotencyKeyRepository;
    private final UserRepository userRepository;
    private final OrderableShopRepository orderableShopRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final TemporaryPaymentRedisRepository temporaryPaymentRedisRepository;
    private final CartRepository cartRepository;
    private final PaymentCancelRepository paymentCancelRepository;

    private static final String PAYMENT_CANCEL_REASON = "코인 서버 오류로 인한 결제 취소";

    @TransactionalEventListener(phase = AFTER_ROLLBACK)
    @Transactional(propagation = REQUIRES_NEW)
    public void paymentRollback(TossPaymentRollBackEvent event) {
        TemporaryPayment temporaryPayment = event.temporaryPayment();
        User user = userRepository.getById(temporaryPayment.getUserId());
        PaymentIdempotencyKey paymentIdempotencyKey = paymentIdempotencyKeyRepository
            .findByUserId(user.getId())
            .map(idempotencyKey -> {
                if (idempotencyKey.isOlderThanExpireDays()) {
                    idempotencyKey.updateIdempotencyKey(UUID.randomUUID().toString());
                }
                return idempotencyKey;
            })
            .orElseGet(() -> paymentIdempotencyKeyRepository.save(
                PaymentIdempotencyKey.builder()
                    .userId(user.getId())
                    .idempotencyKey(UUID.randomUUID().toString())
                    .build()
            ));

        PaymentCancelResponse response = tossPaymentClient.requestCancel(event.paymentKey(),
            PAYMENT_CANCEL_REASON, paymentIdempotencyKey.getIdempotencyKey());

        try {
            OrderableShop orderableShop = orderableShopRepository.getById(temporaryPayment.getOrderableShopId());
            Order order = temporaryPayment.toOrder(user, orderableShop);
            orderRepository.save(order);

            Payment payment = event.tossPaymentConfirmResponse().toEntity(order);
            payment.cancel();
            paymentRepository.save(payment);

            List<PaymentCancel> paymentCancels = response.getPaymentCancels(payment);
            paymentCancelRepository.saveAll(paymentCancels);

            temporaryPaymentRedisRepository.deleteById(order.getId());
            cartRepository.deleteByUserId(user.getId());
        } catch (Exception e) {
            log.error("결제 취소 과정에서 오류 발생 - paymentId: {}, userId: {}, orderId: {}", event.paymentKey(),
                temporaryPayment.getUserId(), temporaryPayment.getOrderId());
        }
    }
}
