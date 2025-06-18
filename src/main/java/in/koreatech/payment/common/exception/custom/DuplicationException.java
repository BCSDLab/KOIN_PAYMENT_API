package in.koreatech.payment.common.exception.custom;

public abstract class DuplicationException extends KoinException {

    protected DuplicationException(String message, String code) {
        super(message, code);
    }

    protected DuplicationException(String message, String detail, String code) {
        super(message, detail, code);
    }
}
