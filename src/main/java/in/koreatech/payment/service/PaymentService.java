package in.koreatech.payment.service;

import java.util.List;

import in.koreatech.payment.model.Payment;
import in.koreatech.payment.model.PaymentCancel;

public interface PaymentService {
    String createTemporaryPayment(String accessToken, Integer amount);
    Payment confirmPayment(String accessToken, String paymentKey, String orderId, Integer amount);
    List<PaymentCancel> cancelPayment(String accessToken, String paymentKey, String cancelReason);
}
