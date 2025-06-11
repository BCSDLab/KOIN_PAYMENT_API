package in.koreatech.payment.client.exception;

public class InternalKoinException extends RuntimeException {
    private final String code;
    private final String traceId;

    public InternalKoinException(String code, String message, String traceId) {
        super(message);
        this.code = code;
        this.traceId = traceId;
    }

    public String getCode() {
        return code;
    }

    public String getTraceId() {
        return traceId;
    }
}
