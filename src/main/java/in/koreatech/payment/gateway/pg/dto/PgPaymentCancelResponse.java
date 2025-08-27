package in.koreatech.payment.gateway.pg.dto;

import java.util.List;

public record PgPaymentCancelResponse(
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
}
