package in.koreatech.payment.client.dto.response;

public record PaymentConfirmResponse(
    String paymentKey,
    String orderId,
    Integer amount
) {
}
