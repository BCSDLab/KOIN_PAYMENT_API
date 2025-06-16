package in.koreatech.payment.client.exception;

public class TossPaymentException extends RuntimeException {

    private final Integer statusCode;
    private final String code;

    private TossPaymentException(String message, Integer statusCode, String code) {
        super(message);
        this.statusCode = statusCode;
        this.code = code;
    }

    public static TossPaymentException of(String message, Integer statusCode, String code) {
        return new TossPaymentException(message, statusCode, code);
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getCode() {
        return code;
    }
}
