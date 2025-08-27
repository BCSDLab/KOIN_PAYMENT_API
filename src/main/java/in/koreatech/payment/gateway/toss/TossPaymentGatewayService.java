package in.koreatech.payment.gateway.toss;

import static in.koreatech.payment.gateway.pg.dto.PaymentCancelResponse.CancelInfo;

import java.util.List;

import org.springframework.stereotype.Service;

import in.koreatech.koin.domain.order.model.PaymentStatus;
import in.koreatech.payment.exception.PaymentCancelException;
import in.koreatech.payment.exception.PaymentConfirmException;
import in.koreatech.payment.gateway.pg.PaymentGatewayService;
import in.koreatech.payment.gateway.pg.dto.PaymentCancelResponse;
import in.koreatech.payment.gateway.pg.dto.PaymentConfirmResponse;
import in.koreatech.payment.gateway.toss.dto.response.TossPaymentCancelResponse;
import in.koreatech.payment.gateway.toss.dto.response.TossPaymentConfirmResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TossPaymentGatewayService implements PaymentGatewayService {

    private final TossPaymentClient tossPaymentClient;

    public PaymentConfirmResponse confirmPayment(String paymentKey, String pgOrderId, Integer amount) {
        TossPaymentConfirmResponse tossPaymentResponse = tossPaymentClient.requestConfirm(paymentKey, pgOrderId,
            amount);
        PaymentStatus paymentStatus = PaymentStatus.valueOf(tossPaymentResponse.status());
        if (!paymentStatus.isDone()) {
            throw PaymentConfirmException.withDetail("paymentStatus : " + tossPaymentResponse.status());
        }

        return new PaymentConfirmResponse(
            tossPaymentResponse.paymentKey(),
            tossPaymentResponse.totalAmount(),
            tossPaymentResponse.orderId(),
            tossPaymentResponse.status(),
            tossPaymentResponse.method(),
            tossPaymentResponse.requestedAt(),
            tossPaymentResponse.approvedAt()
        );
    }

    public PaymentCancelResponse cancelPayment(String paymentKey, String cancelReason, String idempotencyKey) {
        TossPaymentCancelResponse response = tossPaymentClient.requestCancel(paymentKey, cancelReason, idempotencyKey);
        if (!PaymentStatus.valueOf(response.status()).isCanceled()) {
            throw PaymentCancelException.withDetail("paymentStatus : " + response.status());
        }

        return new PaymentCancelResponse(
            response.paymentKey(),
            response.orderId(),
            response.status(),
            response.cancels().stream()
                .map(cancelInfo -> new CancelInfo(
                    cancelInfo.cancelAmount(),
                    cancelInfo.cancelReason(),
                    cancelInfo.canceledAt(),
                    cancelInfo.transactionKey()
                ))
                .toList()
        );
    }
}
