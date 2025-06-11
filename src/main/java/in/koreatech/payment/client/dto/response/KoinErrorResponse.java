package in.koreatech.payment.client.dto.response;

public record KoinErrorResponse(
    String code,
    String message,
    String errorTraceId
) {

}
