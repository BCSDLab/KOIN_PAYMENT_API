package in.koreatech.payment.common.exception.custom;

public abstract class DataNotFoundException extends KoinException {

    protected DataNotFoundException(String message, String code) {
        super(message, code);
    }

    protected DataNotFoundException(String message, String detail, String code) {
        super(message, detail, code);
    }
}
