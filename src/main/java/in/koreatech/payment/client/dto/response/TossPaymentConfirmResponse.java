package in.koreatech.payment.client.dto.response;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import in.koreatech.koin.domain.order.model.Order;
import in.koreatech.koin.domain.order.model.Payment;
import in.koreatech.koin.domain.order.model.PaymentMethod;
import in.koreatech.koin.domain.order.model.PaymentStatus;

public record TossPaymentConfirmResponse(
    String paymentKey,
    Integer totalAmount,
    String status,
    String method,
    String requestedAt,
    String approvedAt
) {
    public Payment toEntity(Order order) {
        OffsetDateTime requestedOffsetDateTime = OffsetDateTime.parse(requestedAt);
        OffsetDateTime approvedOffsetDateTime = OffsetDateTime.parse(approvedAt);

        LocalDateTime requested = requestedOffsetDateTime.toLocalDateTime();
        LocalDateTime approved = approvedOffsetDateTime.toLocalDateTime();

        return Payment.builder()
            .paymentKey(paymentKey)
            .amount(totalAmount)
            .paymentStatus(PaymentStatus.valueOf(status))
            .paymentMethod(PaymentMethod.from(method))
            .requestedAt(requested)
            .approvedAt(approved)
            .order(order)
            .build();
    }
}
