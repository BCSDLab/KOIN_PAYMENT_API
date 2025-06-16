package in.koreatech.payment.client.exception;

public record TossPaymentErrorResponse(
    String code,
    String message
) {
    
}
