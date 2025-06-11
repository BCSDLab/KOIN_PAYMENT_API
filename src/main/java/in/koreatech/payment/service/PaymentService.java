package in.koreatech.payment.service;

public interface PaymentService {
    void saveTemporaryPaymentInformation(Integer userId, Integer amount);
}
