package in.koreatech.payment.client.dto.response;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import in.koreatech.payment.model.entity.Payment;
import in.koreatech.payment.model.enums.PaymentMethod;
import in.koreatech.payment.model.enums.PaymentStatus;

public record PaymentConfirmResponse(
    String paymentKey,
    String orderId,
    Integer amount,
    String status,
    String method,
    String requestedAt,
    String approvedAt
) {
    public Payment toEntity(Integer userId) {
        OffsetDateTime requestedOffsetDateTime = OffsetDateTime.parse(requestedAt);
        OffsetDateTime approvedOffsetDateTime = OffsetDateTime.parse(approvedAt);

        LocalDateTime requested = requestedOffsetDateTime.toLocalDateTime();
        LocalDateTime approved = approvedOffsetDateTime.toLocalDateTime();

        return Payment.builder()
            .paymentKey(paymentKey)
            .orderId(orderId)
            .amount(amount)
            .userId(userId)
            .paymentStatus(PaymentStatus.valueOf(status))
            .paymentMethod(PaymentMethod.from(method))
            .requestedAt(requested)
            .approvedAt(approved)
            .build();
    }
}
