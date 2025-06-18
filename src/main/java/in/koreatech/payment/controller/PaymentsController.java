package in.koreatech.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.koreatech.payment.common.auth.AccessToken;
import in.koreatech.payment.dto.request.PaymentCancelRequest;
import in.koreatech.payment.dto.request.PaymentConfirmRequest;
import in.koreatech.payment.dto.request.TemporaryPaymentInformationSaveRequest;
import in.koreatech.payment.dto.response.TemporaryPaymentResponse;
import in.koreatech.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentsController implements PaymentsApi {

    private final PaymentService paymentService;

    @PostMapping("/temporary")
    public ResponseEntity<TemporaryPaymentResponse> createTemporaryPayment(
        @RequestBody @Valid final TemporaryPaymentInformationSaveRequest request,
        @AccessToken final String accessToken
    ) {
        String orderId = paymentService.createTemporaryPayment(accessToken, request.amount());
        TemporaryPaymentResponse response = TemporaryPaymentResponse.of(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmPayment(
        @RequestBody @Valid final PaymentConfirmRequest request,
        @AccessToken final String accessToken
    ) {
        paymentService.confirmPayment(accessToken, request.paymentKey(), request.orderId(), request.amount());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{paymentKey}/cancel")
    public ResponseEntity<Void> cancelPayment(
        @PathVariable(value = "paymentKey") final String paymentKey,
        @RequestBody @Valid final PaymentCancelRequest request,
        @AccessToken final String accessToken
    ) {
        paymentService.cancelPayment(accessToken, paymentKey, request.cancelReason());
        return ResponseEntity.ok().build();
    }
}
