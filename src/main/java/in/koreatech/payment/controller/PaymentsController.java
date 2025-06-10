package in.koreatech.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.koreatech.payment.dto.request.DeliveryTemporaryPaymentSaveRequest;
import in.koreatech.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentsController implements PaymentsApi {

    private final PaymentService paymentService;

    @PostMapping("/temporary")
    public ResponseEntity<Void> saveTemporaryPayment(
        @RequestBody @Valid final DeliveryTemporaryPaymentSaveRequest request
    ) {
        paymentService.saveTemporaryPayment(request.orderId(), request.userId(), request.amount());
        return ResponseEntity.ok().build();
    }
}
