package in.koreatech.payment.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import in.koreatech.payment.model.entity.Payment;
import in.koreatech.payment.model.entity.PaymentCancel;
import in.koreatech.payment.gateway.pg.dto.PaymentGatewayCancelResponse;

@Component
public class PaymentCancelMapper {

    public List<PaymentCancel> toEntity(Payment payment, PaymentGatewayCancelResponse paymentGatewayCancelResponse) {
        List<PaymentCancel> paymentCancels = new ArrayList<>();
        List<PaymentGatewayCancelResponse.CancelInfo> cancels = paymentGatewayCancelResponse.cancels();

        for (PaymentGatewayCancelResponse.CancelInfo cancelInfo : cancels) {
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
