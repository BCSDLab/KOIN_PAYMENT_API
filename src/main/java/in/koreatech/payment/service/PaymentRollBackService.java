package in.koreatech.payment.service;

import in.koreatech.payment.event.PaymentRollBackEvent;

public interface PaymentRollBackService {
    void paymentRollback(PaymentRollBackEvent event);
}
