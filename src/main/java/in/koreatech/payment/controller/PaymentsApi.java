package in.koreatech.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import in.koreatech.payment.common.auth.AccessToken;
import in.koreatech.payment.dto.request.TemporaryPaymentInformationSaveRequest;
import in.koreatech.payment.dto.response.TemporaryPaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/payments")
@Tag(name = "(Normal) Payments: 결제", description = "결제 API를 관리한다.")
public interface PaymentsApi {

    @Operation(
        summary = "임시 결제 정보를 저장한다",
        description = "사용자의 임시 결제 정보를 저장한다.",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(
                responseCode = "400",
                description = "요청 오류",
                content = @Content(
                    mediaType = "application/json",
                    examples = {
                        @ExampleObject(
                            name = "토큰이 없는 경우",
                            summary = "토큰이 없는 경우",
                            value = """
                        {
                          "code": "",
                          "message": "올바르지 않은 인증정보입니다.",
                          "errorTraceId": "41b38a02-09c8-432d-b381-4e7ae42c4c6b"
                        }
                        """
                        ),
                        @ExampleObject(
                            name = "숫자 입력 필드에 잘못된 값",
                            summary = "숫자 입력 필드에 잘못된 값",
                            value = """
                        {
                          "code": "",
                          "message": "잘못된 입력 형식이거나, 값이 허용된 범위를 초과했습니다.",
                          "errorTraceId": "81e0ac39-d2b0-4af4-9c4f-082b7976a568"
                        }
                        """
                        ),
                        @ExampleObject(
                            name = "결제 금액이 null인 경우",
                            summary = "결제 금액이 null인 경우",
                            value = """
                        {
                          "code": "",
                          "message": "결제 금액은 필수 입력사항입니다.",
                          "errorTraceId": "8a19485b-bc22-461a-9345-11de4c677fb8"
                        }
                        """
                        )
                    }
                )
            )
        }
    )
    @PostMapping("/temporary")
    ResponseEntity<TemporaryPaymentResponse> createTemporaryPayment(
        @RequestBody @Valid final TemporaryPaymentInformationSaveRequest request,
        @AccessToken final String accessToken
    );
}
