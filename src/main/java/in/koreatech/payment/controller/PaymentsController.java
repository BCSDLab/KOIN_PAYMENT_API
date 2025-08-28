package in.koreatech.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.koreatech.payment.common.auth.AccessToken;
import in.koreatech.payment.dto.request.PaymentCancelRequest;
import in.koreatech.payment.dto.request.PaymentConfirmRequest;
import in.koreatech.payment.dto.request.TemporaryDeliveryPaymentSaveRequest;
import in.koreatech.payment.dto.request.TemporaryTakeoutPaymentSaveRequest;
import in.koreatech.payment.dto.response.PaymentCancelResponse;
import in.koreatech.payment.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.dto.response.PaymentResponse;
import in.koreatech.payment.dto.response.TemporaryPaymentResponse;
import in.koreatech.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentsController implements PaymentsApi {

    private final PaymentService paymentService;

    @PostMapping("/delivery/temporary")
    public ResponseEntity<TemporaryPaymentResponse> createTemporaryDeliveryPayment(
        @RequestBody @Valid final TemporaryDeliveryPaymentSaveRequest request,
        @AccessToken final String accessToken
    ) {
        TemporaryPaymentResponse response = paymentService.createTemporaryDeliveryPayment(accessToken, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/takeout/temporary")
    public ResponseEntity<TemporaryPaymentResponse> createTemporaryTakeoutPayment(
        @RequestBody @Valid final TemporaryTakeoutPaymentSaveRequest request,
        @AccessToken final String accessToken
    ) {
        TemporaryPaymentResponse response = paymentService.createTemporaryTakeoutPayment(accessToken, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(
        @RequestBody @Valid final PaymentConfirmRequest request,
        @AccessToken final String accessToken
    ) {
        PaymentConfirmResponse response = paymentService.confirmPayment(accessToken, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentCancelResponse> cancelPayment(
        @PathVariable(value = "paymentId") final Integer paymentId,
        @RequestBody @Valid final PaymentCancelRequest request,
        @AccessToken final String accessToken
    ) {
        PaymentCancelResponse response = paymentService.cancelPayment(accessToken, paymentId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(
        @PathVariable(value = "paymentId") final Integer paymentId,
        @AccessToken final String accessToken
    ) {
        PaymentResponse response = paymentService.getPayment(accessToken, paymentId);
        return ResponseEntity.ok(response);
    }
}
