package in.koreatech.payment.service;

import java.util.List;

import in.koreatech.koin.domain.order.model.PaymentCancel;
import in.koreatech.payment.dto.request.TemporaryDeliveryPaymentSaveRequest;
import in.koreatech.payment.dto.request.TemporaryTakeoutPaymentSaveRequest;
import in.koreatech.payment.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.dto.response.PaymentResponse;

public interface PaymentService {
    String createTemporaryDeliveryPayment(String accessToken, TemporaryDeliveryPaymentSaveRequest request);
    String createTemporaryTakeoutPayment(String accessToken, TemporaryTakeoutPaymentSaveRequest request);
    PaymentConfirmResponse confirmPayment(String accessToken, String paymentKey, String orderId, Integer amount);
    List<PaymentCancel> cancelPayment(String accessToken, Integer paymentId, String cancelReason);
    PaymentResponse getPayment(String accessToken, Integer paymentId);
}
