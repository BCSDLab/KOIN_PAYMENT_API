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
import in.koreatech.payment.dto.request.TemporaryTakeoutPaymentSaveRequest;
import in.koreatech.payment.dto.response.PaymentCancelResponse;
import in.koreatech.payment.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.dto.response.TemporaryPaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RequestMapping("/payments")
@Tag(name = "(Normal) Payments: 결제", description = "결제 API를 관리한다.")
public interface PaymentsApi {

    @Operation(
        summary = "임시 배달 결제 정보를 저장한다",
        description = """
            ## 임시 배달 결제 정보 저장
            토큰을 통해 장바구니를 불러옵니다.
            요청 본문과 장바구니 데이터를 함께 임시 저장합니다. 저장 기간은 10분입니다.
            
            반환 값인 주문번호를 사용하여 로직을 수행해주세요.
            
            ## 요청 Body 필드 설명
            - `address`: 배달 받을 주소 (필수)
            - `phone_number`: 수신자 연락처 (필수)
            - `to_owner`: 사장님께 전달할 메시지 (선택)
            - `to_rider`: 라이더에게 전달할 메시지 (선택)
            - `total_menu_price`: 메뉴 총 금액 (필수)
            - `delivery_tip`: 배달 팁 (필수)
            - `total_amount`: 총 결제 금액 (필수)
            """
    )
    @PostMapping("/delivery/temporary")
    ResponseEntity<TemporaryPaymentResponse> createTemporaryDeliveryPayment(
        @RequestBody @Valid final TemporaryDeliveryPaymentSaveRequest request,
        @AccessToken final String accessToken
    );

    @Operation(
        summary = "임시 포장 결제 정보를 저장한다",
        description = """
            ## 임시 포장 결제 정보 저장
            토큰을 통해 장바구니를 불러옵니다.
            요청 본문과 장바구니 데이터를 함께 임시 저장합니다. 저장 기간은 10분입니다.
            
            반환 값인 주문번호를 사용하여 로직을 수행해주세요.
            
            ## 요청 Body 필드 설명
            - `phone_number`: 수신자 연락처 (필수)
            - `to_owner`: 사장님께 전달할 메시지 (선택)
            - `total_menu_price`: 메뉴 총 금액 (필수)
            - `total_amount`: 총 결제 금액 (필수)
            """
    )
    @PostMapping("/takeout/temporary")
    ResponseEntity<TemporaryPaymentResponse> createTemporaryTakeoutPayment(
        @RequestBody @Valid final TemporaryTakeoutPaymentSaveRequest request,
        @AccessToken final String accessToken
    );

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "결제 승인 성공",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(name = "배달", value = """
                    {
                      "id": 1,
                      "delivery_address": "충청남도 천안시 동남구 병천면 충절로 1600 은솔관 422호",
                      "shop_address": "충청남도 천안시 동남구 병천면 충절로 1600 은솔관 422호",
                      "to_owner": "리뷰 이벤트 감사합니다.",
                      "to_rider": "문 앞에 놔주세요.",
                      "amount": 1000,
                      "shop_name": "굿모닝 살로만 치킨",
                      "menus": [
                        {
                          "name": "허니콤보",
                          "quantity": 1,
                          "options": [
                            {
                              "option_group_name": "소스 추가",
                              "option_name": "레드디핑 소스"
                            }
                          ]
                        }
                      ],
                      "order_type": "DELIVERY",
                      "requested_at": "2025-07-02T13:07:07.359Z",
                      "approved_at": "2025-07-02T13:07:07.360Z",
                      "payment_method": "카드"
                    }
                    """),
                @ExampleObject(name = "포장", value = """
                    {
                      "id": 1,
                      "delivery_address": null,
                      "shop_address": "충청남도 천안시 동남구 병천면 충절로 1600 은솔관 422호",
                      "to_owner": "리뷰 이벤트 감사합니다.",
                      "to_rider": null,
                      "amount": 1000,
                      "shop_name": "굿모닝 살로만 치킨",
                      "menus": [
                        {
                          "name": "허니콤보",
                          "quantity": 1,
                          "options": [
                            {
                              "option_group_name": "소스 추가",
                              "option_name": "레드디핑 소스"
                            }
                          ]
                        }
                      ],
                      "order_type": "TAKE_OUT",
                      "requested_at": "2025-07-02T13:07:07.359Z",
                      "approved_at": "2025-07-02T13:07:07.360Z",
                      "payment_method": "카드"
                    }
                    """)
            })
        )}
    )
    @Operation(
        summary = "결제 승인을 한다.",
        description = """
            ## 결제 승인
            임시 저장된 데이터(orderId, amount)와 요청 본문 데이터(orderId, amount)를 검증합니다.
            검증에 성공하면 토스 페이먼츠에 결제 승인 요청을 합니다.
            결제 승인 요청을 성공하면, 임시 저장 데이터를 DB에 저장하고 장바구니를 초기화합니다.
            
            ## 요청 Body 필드 설명
            - `payment_key`: PG사에서 받은 결제 키 (필수)
            - `order_id`: 주문 고유 번호 (필수)
            - `amount`: 최종 결제 금액 (필수)
            """
    )
    @PostMapping("/confirm")
    ResponseEntity<PaymentConfirmResponse> confirmPayment(
        @RequestBody @Valid final PaymentConfirmRequest request,
        @AccessToken final String accessToken
    );

    @Operation(
        summary = "결제 취소를 한다.",
        description = """
            ## 결제 취소
            결제 키는 URL 경로에 포함되며, 결제 키에 해당하는 결제를 취소합니다.
            취소 사유는 요청 본문에 포함됩니다.
            
            ## Path Variable
            - `paymentKey`: PG사에서 받은 결제 키
            """
    )
    @PostMapping("/{paymentKey}/cancel")
    ResponseEntity<PaymentCancelResponse> cancelPayment(
        @Parameter(description = "결제 키", example = "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1")
        @PathVariable(value = "paymentKey") final String paymentKey,
        @RequestBody @Valid final PaymentCancelRequest request,
        @AccessToken final String accessToken
    );
}
