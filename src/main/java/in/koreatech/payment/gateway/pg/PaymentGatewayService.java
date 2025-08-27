package in.koreatech.payment.gateway.pg;

import in.koreatech.payment.gateway.pg.dto.PaymentCancelResponse;
import in.koreatech.payment.gateway.pg.dto.PaymentConfirmResponse;

public interface PaymentGatewayService {
    PaymentConfirmResponse confirmPayment(String paymentKey, String pgOrderId, Integer amount);
    PaymentCancelResponse cancelPayment(String paymentKey, String cancelReason, String idempotencyKey);
}
