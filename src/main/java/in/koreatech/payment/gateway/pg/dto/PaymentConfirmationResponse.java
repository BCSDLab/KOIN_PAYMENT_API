package in.koreatech.payment.gateway.pg.dto;

public record PaymentConfirmationResponse(
    String paymentKey,
    String orderId,
    String approvedAt
) {

}
