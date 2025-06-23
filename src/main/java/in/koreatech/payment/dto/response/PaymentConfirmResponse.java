package in.koreatech.payment.dto.response;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import in.koreatech.payment.model.entity.Payment;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(value = SnakeCaseStrategy.class)
public record PaymentConfirmResponse(
    @Schema(description = "결제 고유 id", example = "1", requiredMode = REQUIRED)
    Integer id,

    @Schema(description = "결제 금액", example = "1000", requiredMode = REQUIRED)
    Integer amount,

    @Schema(description = "결제 요청 일시", example = "2025.06.21 21:00", requiredMode = REQUIRED)
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    LocalDateTime requestedAt,

    @Schema(description = "결제 승인 일시", example = "2025.06.21 21:00", requiredMode = REQUIRED)
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    LocalDateTime approvedAt,

    @Schema(description = "결제 수단", example = "카드", requiredMode = REQUIRED)
    String paymentMethod
) {
    public static PaymentConfirmResponse from(Payment payment) {
        return new PaymentConfirmResponse(
          payment.getId(),
          payment.getAmount(),
          payment.getRequestedAt(),
          payment.getApprovedAt(),
          payment.getPaymentMethod().getDisplayName()
        );
    }
}
