package in.koreatech.payment.client.dto.request;

public record PaymentConfirmRequest(
    String paymentKey,
    String orderId,
    Integer amount
) {

}
