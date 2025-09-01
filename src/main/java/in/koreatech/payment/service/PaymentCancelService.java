package in.koreatech.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.koin.domain.order.model.Payment;
import in.koreatech.koin.domain.order.model.PaymentCancel;
import in.koreatech.koin.domain.order.model.PaymentStatus;
import in.koreatech.koin.domain.order.repository.PaymentCancelRepository;
import in.koreatech.koin.domain.order.repository.PaymentRepository;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.payment.dto.response.PaymentCancelResponse;
import in.koreatech.payment.exception.PaymentAlreadyCanceledException;
import in.koreatech.payment.exception.PaymentCancelException;
import in.koreatech.payment.gateway.pg.PaymentGatewayService;
import in.koreatech.payment.gateway.pg.dto.PaymentGatewayCancelResponse;
import in.koreatech.payment.mapper.PaymentCancelMapper;
import in.koreatech.payment.model.domain.PaymentCancelInfo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentCancelService {

    private final PaymentRepository paymentRepository;
    private final PaymentCancelRepository paymentCancelRepository;
    private final PaymentGatewayService paymentGatewayService;
    private final PaymentIdempotencyKeyService paymentIdempotencyKeyService;
    private final PaymentCancelMapper paymentCancelMapper;

    @Transactional
    public PaymentCancelResponse cancelPayment(User user, Integer paymentId, PaymentCancelInfo paymentCancelInfo) {
        Payment payment = paymentRepository.getById(paymentId);
        validatePaymentStatusIsNotCanceled(payment);
        payment.validateUserIdMatches(user.getId());

        String paymentIdempotencyKey = paymentIdempotencyKeyService.getOrCreate(user.getId());
        PaymentGatewayCancelResponse pgResponse = paymentGatewayService.cancelPayment(payment.getPaymentKey(), paymentCancelInfo.cancelReason(), paymentIdempotencyKey);
        validatePaymentIsCanceled(pgResponse.status());

        payment.cancel();
        List<PaymentCancel> paymentCancels = paymentCancelMapper.toEntity(payment, pgResponse);
        paymentCancelRepository.saveAll(paymentCancels);

        return PaymentCancelResponse.from(paymentCancels);
    }

    private void validatePaymentStatusIsNotCanceled(Payment payment) {
        if (payment.getPaymentStatus().isCanceled()) {
            throw PaymentAlreadyCanceledException.withDetail("paymentId : " + payment.getId());
        }
    }

    private void validatePaymentIsCanceled(String status) {
        if (!PaymentStatus.valueOf(status).isCanceled()) {
            throw PaymentCancelException.withDetail("paymentStatus : " + status);
        }
    }
}
