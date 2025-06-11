package in.koreatech.payment.client.exception;

public record InternalKoinErrorResponse(
    String code,
    String message,
    String errorTraceId
) {

}
