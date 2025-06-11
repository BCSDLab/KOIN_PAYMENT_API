package in.koreatech.payment.client.exception;

import org.springframework.http.HttpStatus;

public class InternalKoinException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String code;
    private final String traceId;

    public InternalKoinException(HttpStatus status, String code, String message, String traceId) {
        super(message);
        this.httpStatus = status;
        this.code = code;
        this.traceId = traceId;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getTraceId() {
        return traceId;
    }
}
