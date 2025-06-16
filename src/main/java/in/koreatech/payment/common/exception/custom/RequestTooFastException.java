package in.koreatech.payment.common.exception.custom;

public class RequestTooFastException extends KoinException {

    public RequestTooFastException(String message) {
        super(message);
    }
}
