package in.koreatech.payment.gateway.toss;

import java.util.List;

import org.springframework.stereotype.Service;

import in.koreatech.koin.domain.order.model.PaymentStatus;
import in.koreatech.payment.exception.PaymentConfirmException;
import in.koreatech.payment.gateway.pg.PaymentGatewayService;
import in.koreatech.payment.gateway.pg.dto.PaymentCancelResponse;
import in.koreatech.payment.gateway.pg.dto.PaymentConfirmationResponse;
import in.koreatech.payment.gateway.toss.dto.response.TossPaymentConfirmResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TossPaymentGatewayService implements PaymentGatewayService {

    private final TossPaymentClient tossPaymentClient;

    public PaymentConfirmationResponse confirmPayment(String paymentKey, String pgOrderId, Integer amount) {
        TossPaymentConfirmResponse tossPaymentResponse = tossPaymentClient.requestConfirm(paymentKey, pgOrderId, amount);
        PaymentStatus paymentStatus = PaymentStatus.valueOf(tossPaymentResponse.status());
        if (!paymentStatus.isDone()) {
            throw PaymentConfirmException.withDetail("paymentStatus : " + tossPaymentResponse.status());
        }

        return new PaymentConfirmationResponse(
            tossPaymentResponse.paymentKey(),
            tossPaymentResponse.totalAmount(),
            tossPaymentResponse.orderId(),
            tossPaymentResponse.status(),
            tossPaymentResponse.method(),
            tossPaymentResponse.requestedAt(),
            tossPaymentResponse.approvedAt()
        );
    }

    public List<PaymentCancelResponse> cancelPayment(String paymentKey, String cancelReason) {
        return List.of();
    }
}
