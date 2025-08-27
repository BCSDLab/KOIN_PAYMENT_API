package in.koreatech.payment.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.order.model.Payment;
import in.koreatech.koin.domain.order.model.PaymentCancel;
import in.koreatech.payment.gateway.pg.dto.PgPaymentCancelResponse;

@Component
public class PaymentCancelMapper {

    public List<PaymentCancel> toEntity(Payment payment, PgPaymentCancelResponse pgPaymentCancelResponse) {
        List<PaymentCancel> paymentCancels = new ArrayList<>();
        List<PgPaymentCancelResponse.CancelInfo> cancels = pgPaymentCancelResponse.cancels();

        for (PgPaymentCancelResponse.CancelInfo cancelInfo : cancels) {
            OffsetDateTime cancelOffsetDateTime = OffsetDateTime.parse(cancelInfo.canceledAt());
            LocalDateTime canceled = cancelOffsetDateTime.toLocalDateTime();

            paymentCancels.add(PaymentCancel.builder()
                .transactionKey(cancelInfo.transactionKey())
                .cancelReason(cancelInfo.cancelReason())
                .cancelAmount(cancelInfo.cancelAmount())
                .canceledAt(canceled)
                .payment(payment)
                .build());
        }

        return paymentCancels;
    }
}
