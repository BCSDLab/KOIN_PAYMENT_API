package in.koreatech.payment.gateway.pg.dto;

public record PaymentCancelResponse(
    String cancelReason,
    String canceledAt,
    Integer cancelAmount
) {

}
