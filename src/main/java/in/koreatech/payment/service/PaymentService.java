package in.koreatech.payment.service;

public interface PaymentService {
    void saveDeliveryTemporaryPayment(String orderId, Integer userId, Integer amount);
}
