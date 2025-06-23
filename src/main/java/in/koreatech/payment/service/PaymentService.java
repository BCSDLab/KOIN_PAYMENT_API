package in.koreatech.payment.service;

import java.util.List;

import in.koreatech.payment.dto.request.TemporaryDeliveryPaymentSaveRequest;
import in.koreatech.payment.dto.request.TemporaryTakeoutPaymentSaveRequest;
import in.koreatech.payment.model.entity.Payment;
import in.koreatech.payment.model.entity.PaymentCancel;

public interface PaymentService {
    String createTemporaryDeliveryPayment(String accessToken, TemporaryDeliveryPaymentSaveRequest request);
    String createTemporaryTakeoutPayment(String accessToken, TemporaryTakeoutPaymentSaveRequest request);
    Payment confirmPayment(String accessToken, String paymentKey, String orderId, Integer amount);
    List<PaymentCancel> cancelPayment(String accessToken, String paymentKey, String cancelReason);
}
