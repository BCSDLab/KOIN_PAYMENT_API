package in.koreatech.payment.event;

import in.koreatech.payment.model.redis.TemporaryPayment;

public record PaymentRollBackEvent(
    String paymentKey,
    TemporaryPayment temporaryPayment
) {

}
