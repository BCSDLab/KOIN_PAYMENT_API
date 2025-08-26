package in.koreatech.payment.gateway.pg.dto;

public record PaymentConfirmationResponse(
    String paymentKey,
    Integer totalAmount,
    String orderId,
    String status,
    String method,
    String requestedAt,
    String approvedAt
) {

}
