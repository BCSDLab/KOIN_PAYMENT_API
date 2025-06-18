package in.koreatech.payment.client.dto;

public record PaymentConfirmResponse(
    String paymentKey,
    String orderId,
    Integer amount
) {
}
