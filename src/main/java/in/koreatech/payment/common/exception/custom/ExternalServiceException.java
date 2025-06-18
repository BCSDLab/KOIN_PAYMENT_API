package in.koreatech.payment.common.exception.custom;

public abstract class ExternalServiceException extends KoinException {

    protected ExternalServiceException(String message, String code) {
        super(message, code);
    }

    protected ExternalServiceException(String message, String detail, String code) {
        super(message, detail, code);
    }
}
