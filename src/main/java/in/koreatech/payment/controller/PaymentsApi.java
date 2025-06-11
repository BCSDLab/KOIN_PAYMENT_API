package in.koreatech.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import in.koreatech.payment.common.auth.UserId;
import in.koreatech.payment.dto.request.TemporaryPaymentInformationSaveRequest;
import in.koreatech.payment.dto.response.TemporaryDeliverPaymentSaveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/payments")
@Tag(name = "(Normal) Payments: 결제", description = "결제 API를 관리한다.")
public interface PaymentsApi {

    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
        }
    )
    @Operation(summary = "임시 결제 정보를 저장한다")
    @PostMapping("/temporary")
    ResponseEntity<TemporaryDeliverPaymentSaveResponse> saveTemporaryPaymentInformation(
        @RequestBody @Valid final TemporaryPaymentInformationSaveRequest request,
        @UserId final Integer userId
    );
}
