package in.koreatech.payment.toss.service;

import in.koreatech.payment.toss.dto.request.TemporaryPaymentSaveRequest;

public interface PaymentService {
    void saveTemporaryPayment(TemporaryPaymentSaveRequest request);
}
