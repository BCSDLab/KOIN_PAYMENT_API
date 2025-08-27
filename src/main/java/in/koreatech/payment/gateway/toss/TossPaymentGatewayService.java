package in.koreatech.payment.gateway.toss;

import static in.koreatech.payment.gateway.pg.dto.PgPaymentCancelResponse.CancelInfo;

import org.springframework.stereotype.Service;

import in.koreatech.payment.gateway.pg.PaymentGatewayService;
import in.koreatech.payment.gateway.pg.dto.PgPaymentCancelResponse;
import in.koreatech.payment.gateway.pg.dto.PgPaymentConfirmResponse;
import in.koreatech.payment.gateway.toss.dto.response.TossPaymentCancelResponse;
import in.koreatech.payment.gateway.toss.dto.response.TossPaymentConfirmResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TossPaymentGatewayService implements PaymentGatewayService {

    private final TossPaymentClient tossPaymentClient;

    public PgPaymentConfirmResponse confirmPayment(String paymentKey, String pgOrderId, Integer amount) {
        TossPaymentConfirmResponse tossPaymentConfirmResponse = tossPaymentClient.requestConfirm(paymentKey, pgOrderId,
            amount);

        return new PgPaymentConfirmResponse(
            tossPaymentConfirmResponse.paymentKey(),
            tossPaymentConfirmResponse.totalAmount(),
            tossPaymentConfirmResponse.orderId(),
            tossPaymentConfirmResponse.status(),
            tossPaymentConfirmResponse.method(),
            tossPaymentConfirmResponse.requestedAt(),
            tossPaymentConfirmResponse.approvedAt()
        );
    }

    public PgPaymentCancelResponse cancelPayment(String paymentKey, String cancelReason, String idempotencyKey) {
        TossPaymentCancelResponse tossPaymentCancelResponse = tossPaymentClient.requestCancel(paymentKey, cancelReason, idempotencyKey);

        return new PgPaymentCancelResponse(
            tossPaymentCancelResponse.paymentKey(),
            tossPaymentCancelResponse.orderId(),
            tossPaymentCancelResponse.status(),
            tossPaymentCancelResponse.cancels().stream()
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
