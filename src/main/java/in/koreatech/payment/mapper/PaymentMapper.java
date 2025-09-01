package in.koreatech.payment.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import org.springframework.stereotype.Component;

import in.koreatech.payment.model.entity.Order;
import in.koreatech.payment.model.entity.Payment;
import in.koreatech.payment.model.entity.PaymentMethod;
import in.koreatech.payment.model.entity.PaymentStatus;
import in.koreatech.payment.gateway.pg.dto.PaymentGatewayConfirmResponse;

@Component
public class PaymentMapper {

    public Payment toEntity(Order order, PaymentGatewayConfirmResponse response) {
        OffsetDateTime requestedOffsetDateTime = OffsetDateTime.parse(response.requestedAt());
        OffsetDateTime approvedOffsetDateTime = OffsetDateTime.parse(response.approvedAt());

        LocalDateTime requested = requestedOffsetDateTime.toLocalDateTime();
        LocalDateTime approved = approvedOffsetDateTime.toLocalDateTime();

        return Payment.builder()
            .paymentKey(response.paymentKey())
            .amount(response.totalAmount())
            .paymentStatus(PaymentStatus.valueOf(response.status()))
            .paymentMethod(PaymentMethod.from(response.method()))
            .requestedAt(requested)
            .approvedAt(approved)
            .order(order)
            .build();
    }
}
