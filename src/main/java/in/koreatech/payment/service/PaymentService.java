package in.koreatech.payment.service;

public interface PaymentService {
    void saveTemporaryPayment(String orderId, Integer userId, Integer amount);
}
