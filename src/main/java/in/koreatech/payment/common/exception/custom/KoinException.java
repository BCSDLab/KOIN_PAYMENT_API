package in.koreatech.payment.common.exception.custom;

public abstract class KoinException extends RuntimeException {

    protected final String detail;
    protected final String code;

    protected KoinException(String message, String code) {
        super(message);
        this.detail = null;
        this.code = code;
    }

    protected KoinException(String message, String detail, String code) {
        super(message);
        this.detail = detail;
        this.code = code;
    }

    public String getFullMessage() {
        return String.format("%s %s", getMessage(), detail);
    }

    public String getCode() {
        return code;
    }
}
