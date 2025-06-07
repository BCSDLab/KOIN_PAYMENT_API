package in.koreatech.payment.service;

import in.koreatech.payment.dto.request.TemporaryPaymentSaveRequest;

public interface PaymentService {
    void saveTemporaryPayment(TemporaryPaymentSaveRequest request);
}
