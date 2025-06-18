package in.koreatech.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.koin.domain.user.model.User;
import in.koreatech.koin.domain.user.repository.UserRepository;
import in.koreatech.payment.client.TossPaymentClient;
import in.koreatech.payment.client.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.common.auth.JwtTokenResolver;
import in.koreatech.payment.model.Payment;
import in.koreatech.payment.model.PaymentIdempotencyKey;
import in.koreatech.payment.model.TemporaryPayment;
import in.koreatech.payment.repository.PaymentIdempotencyKeyRepository;
import in.koreatech.payment.repository.PaymentRepository;
import in.koreatech.payment.repository.TemporaryPaymentRepository;
import in.koreatech.payment.util.OrderIdGenerator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TossService implements PaymentService {

    private final TemporaryPaymentRepository temporaryPaymentRepository;
    private final OrderIdGenerator orderIdGenerator;
    private final JwtTokenResolver jwtTokenResolver;
    private final UserRepository userRepository;
    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;
    private final PaymentIdempotencyKeyRepository paymentIdempotencyKeyRepository;

    @Transactional
    public String createTemporaryPayment(String accessToken, Integer amount) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        User user = userRepository.getById(userId);
        String orderId = orderIdGenerator.generateOrderId();
        TemporaryPayment temporaryPayment = temporaryPaymentRepository.save(
            TemporaryPayment.of(orderId, user.getId(), amount));
        return temporaryPayment.getOrderId();
    }

    @Transactional
    public void confirmPayment(String accessToken, String paymentKey, String orderId, Integer amount) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        User user = userRepository.getById(userId);
        TemporaryPayment temporaryPayment = temporaryPaymentRepository.getByOrderId(orderId);
        temporaryPayment.validateMatches(orderId, user.getId(), amount);
        PaymentConfirmResponse response = tossPaymentClient.requestConfirm(paymentKey, orderId, amount);
        paymentRepository.save(Payment.builder()
            .paymentKey(response.paymentKey())
            .orderId(response.orderId())
            .amount(response.amount())
            .userId(user.getId())
            .build());
        temporaryPaymentRepository.deleteById(orderId);
    }

    @Transactional
    public void cancelPayment(String accessToken, String paymentKey, String cancelReason) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        User user = userRepository.getById(userId);
        Payment payment = paymentRepository.getByPaymentKey(paymentKey);
        payment.validateUserIdMatches(user.getId());
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

        tossPaymentClient.requestCancel(paymentKey, cancelReason, paymentIdempotencyKey.getIdempotencyKey());
    }
}
