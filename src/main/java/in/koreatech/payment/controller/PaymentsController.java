package in.koreatech.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.koreatech.payment.common.auth.UserId;
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
        @UserId final Integer userId
    ) {
        String orderId = paymentService.createTemporaryPayment(userId, request.amount());
        TemporaryPaymentResponse response = TemporaryPaymentResponse.of(orderId);
        return ResponseEntity.ok(response);
    }
}
