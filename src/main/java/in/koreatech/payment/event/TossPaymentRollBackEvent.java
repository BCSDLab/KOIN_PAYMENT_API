package in.koreatech.payment.event;

import in.koreatech.payment.client.dto.response.TossPaymentConfirmResponse;
import in.koreatech.payment.model.redis.TemporaryPayment;

public record TossPaymentRollBackEvent(
    String paymentKey,
    TemporaryPayment temporaryPayment,
    TossPaymentConfirmResponse tossPaymentConfirmResponse
) {
    public static TossPaymentRollBackEvent from(String paymentKey, TemporaryPayment temporaryPayment, TossPaymentConfirmResponse tossPaymentConfirmResponse) {
        return new TossPaymentRollBackEvent(paymentKey, temporaryPayment, tossPaymentConfirmResponse);
    }
}
