package in.koreatech.payment.service;

public interface PaymentService {
    void saveTemporaryPaymentInformation(String orderId, Integer userId, Integer amount);
}
