package in.koreatech.payment.client.dto;

public record TossPaymentConfirmRequest(
    String paymentKey,
    String orderId,
    Integer amount
) {

}
