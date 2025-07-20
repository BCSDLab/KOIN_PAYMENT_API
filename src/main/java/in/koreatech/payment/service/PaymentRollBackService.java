package in.koreatech.payment.service;

import in.koreatech.payment.event.TossPaymentRollBackEvent;

public interface PaymentRollBackService {
    void paymentRollback(TossPaymentRollBackEvent event);
}
