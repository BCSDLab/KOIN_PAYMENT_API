package in.koreatech.payment.gateway.toss.dto.request;

public record PaymentConfirmRequest(
    String paymentKey,
    String orderId,
    Integer amount
) {

}
