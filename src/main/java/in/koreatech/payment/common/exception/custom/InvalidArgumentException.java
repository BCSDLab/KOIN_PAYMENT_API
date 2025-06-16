package in.koreatech.payment.common.exception.custom;

public abstract class InvalidArgumentException extends KoinException {

    protected InvalidArgumentException(String message, String code) {
        super(message, code);
    }

    protected InvalidArgumentException(String message, String detail, String code) {
        super(message, detail, code);
    }
}
