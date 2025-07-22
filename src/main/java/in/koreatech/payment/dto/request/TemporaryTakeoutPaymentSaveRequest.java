package in.koreatech.payment.dto.request;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(value = SnakeCaseStrategy.class)
public record TemporaryTakeoutPaymentSaveRequest(
    @Schema(description = "연락처", example = "01012345678", requiredMode = REQUIRED)
    @NotBlank(message = "연락처는 필수 입력사항입니다.")
    String phoneNumber,

    @Schema(description = "사장님에게", example = "리뷰 이벤트 감사합니다.", requiredMode = NOT_REQUIRED)
    String toOwner,

    @Schema(description = "메뉴 총 금액", example = "1234", requiredMode = REQUIRED)
    @NotNull(message = "메뉴 총 금액은 필수 입력사항입니다.")
    Integer totalMenuPrice,

    @Schema(description = "결제 금액", example = "10000", requiredMode = REQUIRED)
    @NotNull(message = "결제 금액은 필수 입력사항입니다.")
    Integer totalAmount
) {
    public TemporaryTakeoutPaymentSaveRequest {
        if (toOwner == null) {
            toOwner = "";
        }
    }
}
