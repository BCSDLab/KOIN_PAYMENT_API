package in.koreatech.payment.gateway.pg;

import in.koreatech.payment.gateway.pg.dto.PgPaymentCancelResponse;
import in.koreatech.payment.gateway.pg.dto.PgPaymentConfirmResponse;

public interface PaymentGatewayService {
    PgPaymentConfirmResponse confirmPayment(String paymentKey, String pgOrderId, Integer amount);
    PgPaymentCancelResponse cancelPayment(String paymentKey, String cancelReason, String idempotencyKey);
}
