package in.koreatech.payment.dto.request;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonNaming(value = SnakeCaseStrategy.class)
public record TemporaryDeliveryPaymentSaveRequest(
    @Schema(description = "장바구니 ID", example = "1", requiredMode = REQUIRED)
    @NotNull(message = "장바구니 ID는 필수 입력사항입니다.")
    Integer cartId,

    @Schema(description = "배달 주소", example = "충청남도 천안시 동남구 병천면 충절로 1600 은솔관 422호", requiredMode = REQUIRED)
    @NotBlank(message = "배달 주소는 필수 입력사항입니다.")
    String address,

    @Schema(description = "연락처", example = "01012345678", requiredMode = REQUIRED)
    @NotBlank(message = "연락처는 필수 입력사항입니다.")
    String phoneNumber,

    @Schema(description = "사장님에게", example = "리뷰 이벤트 감사합니다.", requiredMode = NOT_REQUIRED)
    String toOwner,

    @Schema(description = "라이더에게", example = "문 앞에 놔주세요.", requiredMode = NOT_REQUIRED)
    String toRider,

    @Schema(description = "메뉴 총 금액", example = "1234", requiredMode = REQUIRED)
    @NotNull(message = "메뉴 총 금액은 필수 입력사항입니다.")
    Integer totalMenuPrice,

    @Schema(description = "배달 팁", example = "1234", requiredMode = REQUIRED)
    @NotNull(message = "배달 팁은 필수 입력사항입니다.")
    Integer deliveryTip,

    @Schema(description = "결제 금액", example = "10000", requiredMode = REQUIRED)
    @NotNull(message = "결제 금액은 필수 입력사항입니다.")
    Integer totalAmount
) {

}
