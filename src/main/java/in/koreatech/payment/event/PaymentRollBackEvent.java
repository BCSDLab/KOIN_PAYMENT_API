package in.koreatech.payment.event;

import in.koreatech.payment.client.dto.response.TossPaymentConfirmResponse;
import in.koreatech.payment.model.redis.TemporaryPayment;

public record PaymentRollBackEvent(
    String paymentKey,
    TemporaryPayment temporaryPayment,
    TossPaymentConfirmResponse tossPaymentConfirmResponse
) {
    public static PaymentRollBackEvent from(String paymentKey, TemporaryPayment temporaryPayment, TossPaymentConfirmResponse tossPaymentConfirmResponse) {
        return new PaymentRollBackEvent(paymentKey, temporaryPayment, tossPaymentConfirmResponse);
    }
}
