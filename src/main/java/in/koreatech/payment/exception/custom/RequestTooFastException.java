package in.koreatech.payment.exception.custom;

public class RequestTooFastException extends KoinException {

    public RequestTooFastException(String message) {
        super(message);
    }
}
