package in.koreatech.payment.gateway.pg;

import java.util.List;

import in.koreatech.payment.gateway.pg.dto.PaymentCancelResponse;
import in.koreatech.payment.gateway.pg.dto.PaymentConfirmationResponse;

public interface PaymentGatewayService {
    PaymentConfirmationResponse confirmPayment(String paymentKey, String pgOrderId, Integer amount);
    List<PaymentCancelResponse> cancelPayment(String paymentKey, String cancelReason);
}
