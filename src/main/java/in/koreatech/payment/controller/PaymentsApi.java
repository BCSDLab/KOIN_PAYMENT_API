package in.koreatech.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import in.koreatech.payment.common.auth.AccessToken;
import in.koreatech.payment.dto.request.PaymentCancelRequest;
import in.koreatech.payment.dto.request.PaymentConfirmRequest;
import in.koreatech.payment.dto.request.TemporaryDeliveryPaymentSaveRequest;
import in.koreatech.payment.dto.response.PaymentCancelResponse;
import in.koreatech.payment.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.dto.response.TemporaryPaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/payments")
@Tag(name = "(Normal) Payments: 결제", description = "결제 API를 관리한다.")
public interface PaymentsApi {

    @Operation(
        summary = "임시 배달 결제 정보를 저장한다",
        description = "임시 배달 결제 정보를 저장한다."
    )
    @PostMapping("/temporary")
    ResponseEntity<TemporaryPaymentResponse> createTemporaryDeliveryPayment(
        @RequestBody @Valid final TemporaryDeliveryPaymentSaveRequest request,
        @AccessToken final String accessToken
    );

    // @Operation(
    //     summary = "결제 승인을 한다.",
    //     description = "결제 승인을 한다."
    // )
    // @PostMapping("/confirm")
    // ResponseEntity<PaymentConfirmResponse> confirmPayment(
    //     @RequestBody @Valid final PaymentConfirmRequest request,
    //     @AccessToken final String accessToken
    // );

    @Operation(
        summary = "결제 취소를 한다.",
        description = "결제 취소를 한다."
    )
    @PostMapping("/{paymentKey}/cancel")
    ResponseEntity<PaymentCancelResponse> cancelPayment(
        @Parameter(description = "결제 키", example = "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1")
        @PathVariable(value = "paymentKey") final String paymentKey,
        @RequestBody @Valid final PaymentCancelRequest request,
        @AccessToken final String accessToken
    );
}
