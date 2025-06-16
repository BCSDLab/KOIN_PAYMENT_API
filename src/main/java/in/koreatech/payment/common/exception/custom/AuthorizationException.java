package in.koreatech.payment.common.exception.custom;

public abstract class AuthorizationException extends KoinException {

    protected AuthorizationException(String message) {super(message);}

    protected AuthorizationException(String message, String detail) {super(message, detail);}
}
