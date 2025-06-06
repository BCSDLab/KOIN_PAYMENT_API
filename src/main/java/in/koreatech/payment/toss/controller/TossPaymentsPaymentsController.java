package in.koreatech.payment.toss.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.koreatech.payment.toss.dto.request.TemporaryPaymentSaveRequest;
import in.koreatech.payment.toss.service.TossService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/toss/payments")
public class TossPaymentsPaymentsController implements TossPaymentsApi {

    private final TossService tossService;

    @PostMapping("/temporary")
    public ResponseEntity<Void> saveTemporaryPayment(
        @RequestBody @Valid TemporaryPaymentSaveRequest request
    ) {
        tossService.saveTemporaryPayment(request);
        return ResponseEntity.ok().build();
    }
}
