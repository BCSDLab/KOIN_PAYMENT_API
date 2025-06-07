package in.koreatech.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.koreatech.payment.dto.request.TemporaryPaymentSaveRequest;
import in.koreatech.payment.service.TossService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentsController implements PaymentsApi {

    private final TossService tossService;

    @PostMapping("/temporary")
    public ResponseEntity<Void> saveTemporaryPayment(
        @RequestBody @Valid TemporaryPaymentSaveRequest request
    ) {
        tossService.saveTemporaryPayment(request);
        return ResponseEntity.ok().build();
    }
}
