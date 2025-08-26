package in.koreatech.payment.gateway.toss.dto.response;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import in.koreatech.koin.domain.order.model.Payment;
import in.koreatech.koin.domain.order.model.PaymentCancel;

public record PaymentCancelResponse(
    String paymentKey,
    String orderId,
    String status,
    List<CancelInfo> cancels
) {
    public record CancelInfo(
        Integer cancelAmount,
        String cancelReason,
        String canceledAt,
        String transactionKey
    ) {

    }

    public List<PaymentCancel> getPaymentCancels(Payment payment) {
        List<PaymentCancel> paymentCancels = new ArrayList<>();

        for (CancelInfo cancelInfo : cancels) {
            OffsetDateTime cancelOffsetDateTime = OffsetDateTime.parse(cancelInfo.canceledAt);
            LocalDateTime canceled = cancelOffsetDateTime.toLocalDateTime();

            paymentCancels.add(PaymentCancel.builder()
                .transactionKey(cancelInfo.transactionKey)
                .cancelReason(cancelInfo.cancelReason)
                .cancelAmount(cancelInfo.cancelAmount)
                .canceledAt(canceled)
                .payment(payment)
                .build());
        }

        return paymentCancels;
    }
}
