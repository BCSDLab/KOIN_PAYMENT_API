package in.koreatech.payment.common.exception.custom;

public abstract class AuthorizationException extends KoinException {

    protected AuthorizationException(String message, String code) {super(message, code);}

    protected AuthorizationException(String message, String detail, String code) {super(message, detail, code);}
}
