package in.koreatech.payment.service;

public interface PaymentService {
    String createTemporaryPayment(String accessToken, Integer amount);
}
