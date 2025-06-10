package in.koreatech.payment.dto.request;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

// TODO. 변수명 변경
@JsonNaming(value = SnakeCaseStrategy.class)
public record TemporaryPaymentSaveRequest(
    @Schema(description = "상점 고유 id", example = "1", requiredMode = REQUIRED)
    @NotNull(message = "상점 고유 id는 필수 이력사항입니다.")
    Integer shopId,

    @Schema(description = "배달 주소", example = "충청남도 천안시 동남구 병천면 충절로 1600 은솔관 422호", requiredMode = REQUIRED)
    @NotBlank(message = "배달 주소는 필수 입력사항입니다.")
    String deliveryLocation,

    @Schema(description = "사장님에게", example = "리뷰 이벤트 감사합니다", requiredMode = REQUIRED)
    @NotNull(message = "사장님에게는 필수 입력사항입니다.")
    String ownerMessage,

    @Schema(description = "배달기사님에게", example = "문앞에 놔주세요", requiredMode = REQUIRED)
    @NotNull(message = "배달기사님에게는 필수 입력사항입니다.")
    String riderMessage,

    @Schema(description = "메뉴 리스트", requiredMode = REQUIRED)
    @NotEmpty(message = "메뉴 리스트는 필수 입력사항입니다.")
    List<InnerMenuInfo> menuInfos,

    @Schema(description = "결제 금액", example = "10000", requiredMode = REQUIRED)
    @NotNull(message = "결제 금액은 필수 입력사항입니다.")
    Integer amount
) {

    @JsonNaming(value = SnakeCaseStrategy.class)
    public record InnerMenuInfo(
        @Schema(description = "메뉴 이름", example = "족발 막국수 저녁 set 1개", requiredMode = REQUIRED)
        @NotBlank(message = "메뉴 이름은 필수 입력사항입니다.")
        String name,

        @Schema(description = "메뉴 수량", example = "1", requiredMode = REQUIRED)
        @NotNull(message = "메뉴 수량은 필수 입력사항입니다.")
        Integer quantity
    ) {

    }
}
