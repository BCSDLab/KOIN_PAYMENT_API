package in.koreatech.payment.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.koin.domain.user.model.User;
import in.koreatech.koin.domain.user.repository.UserRepository;
import in.koreatech.payment.client.TossPaymentClient;
import in.koreatech.payment.client.dto.response.PaymentCancelResponse;
import in.koreatech.payment.client.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.common.auth.JwtTokenResolver;
import in.koreatech.payment.common.exception.custom.KoinIllegalStateException;
import in.koreatech.payment.exception.PaymentAlreadyCanceledException;
import in.koreatech.payment.exception.PaymentConfirmException;
import in.koreatech.payment.model.Payment;
import in.koreatech.payment.model.PaymentCancel;
import in.koreatech.payment.model.PaymentIdempotencyKey;
import in.koreatech.payment.model.PaymentStatus;
import in.koreatech.payment.model.TemporaryPayment;
import in.koreatech.payment.repository.PaymentCancelRepository;
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
    private final PaymentCancelRepository paymentCancelRepository;

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
    public Payment confirmPayment(String accessToken, String paymentKey, String orderId, Integer amount) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        User user = userRepository.getById(userId);
        TemporaryPayment temporaryPayment = temporaryPaymentRepository.getByOrderId(orderId);
        temporaryPayment.validateMatches(orderId, user.getId(), amount);

        PaymentConfirmResponse response = tossPaymentClient.requestConfirm(paymentKey, orderId, amount);
        PaymentStatus paymentStatus = PaymentStatus.valueOf(response.status());
        if (!paymentStatus.isDone()) {
            throw new KoinIllegalStateException("서버 에러가 발생했습니다. 관리자에게 문의해주세요.");
        }

        Payment payment = response.toEntity(user.getId());
        paymentRepository.save(payment);
        temporaryPaymentRepository.deleteById(orderId);
        return payment;
    }

    @Transactional
    public List<PaymentCancel> cancelPayment(String accessToken, String paymentKey, String cancelReason) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        User user = userRepository.getById(userId);
        Payment payment = paymentRepository.getByPaymentKey(paymentKey);
        if (payment.getPaymentStatus().isCanceled()) {
            throw PaymentAlreadyCanceledException.withDetail("paymentId : " + payment.getId());
        }
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

        PaymentCancelResponse response = tossPaymentClient.requestCancel(paymentKey, cancelReason,
            paymentIdempotencyKey.getIdempotencyKey());
        if (!PaymentStatus.valueOf(response.status()).isCanceled()) {
            throw new KoinIllegalStateException("서버 에러가 발생했습니다. 관리자에게 문의해주세요.");
        }

        payment.cancel();
        List<PaymentCancel> paymentCancels = response.getPaymentCancels(payment);
        paymentCancelRepository.saveAll(paymentCancels);
        return paymentCancels;
    }
}
