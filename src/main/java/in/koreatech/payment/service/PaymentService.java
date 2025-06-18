package in.koreatech.payment.service;

public interface PaymentService {
    String createTemporaryPayment(String accessToken, Integer amount);
    void confirmPayment(String accessToken, String paymentKey, String orderId, Integer amount);
    void cancelPayment(String accessToken, String paymentKey, String cancelReason);
}
