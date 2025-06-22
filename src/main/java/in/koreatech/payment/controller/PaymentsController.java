package in.koreatech.payment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.koreatech.payment.common.auth.AccessToken;
import in.koreatech.payment.dto.request.PaymentCancelRequest;
import in.koreatech.payment.dto.request.PaymentConfirmRequest;
import in.koreatech.payment.dto.request.TemporaryDeliveryPaymentSaveRequest;
import in.koreatech.payment.dto.response.PaymentCancelResponse;
import in.koreatech.payment.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.dto.response.TemporaryPaymentResponse;
import in.koreatech.payment.model.Payment;
import in.koreatech.payment.model.PaymentCancel;
import in.koreatech.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentsController implements PaymentsApi {

    private final PaymentService paymentService;

    @PostMapping("/temporary")
    public ResponseEntity<TemporaryPaymentResponse> createTemporaryDeliveryPayment(
        @RequestBody @Valid final TemporaryDeliveryPaymentSaveRequest request,
        @AccessToken final String accessToken
    ) {
        String orderId = paymentService.createTemporaryDeliveryPayment(accessToken, request);
        TemporaryPaymentResponse response = TemporaryPaymentResponse.of(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(
        @RequestBody @Valid final PaymentConfirmRequest request,
        @AccessToken final String accessToken
    ) {
        Payment payment = paymentService.confirmPayment(accessToken, request.paymentKey(), request.orderId(),
            request.amount());
        PaymentConfirmResponse response = PaymentConfirmResponse.from(payment);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentKey}/cancel")
    public ResponseEntity<PaymentCancelResponse> cancelPayment(
        @PathVariable(value = "paymentKey") final String paymentKey,
        @RequestBody @Valid final PaymentCancelRequest request,
        @AccessToken final String accessToken
    ) {
        List<PaymentCancel> paymentCancels = paymentService.cancelPayment(accessToken, paymentKey,
            request.cancelReason());
        PaymentCancelResponse response = PaymentCancelResponse.from(paymentCancels);
        return ResponseEntity.ok(response);
    }
}
