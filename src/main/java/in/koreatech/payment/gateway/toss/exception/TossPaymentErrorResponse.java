package in.koreatech.payment.gateway.toss.exception;

public record TossPaymentErrorResponse(
    String code,
    String message
) {
    
}
