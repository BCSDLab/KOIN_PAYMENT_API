package in.koreatech.payment.common.exception.custom;

public abstract class AuthenticationException extends KoinException {

    protected AuthenticationException(String message, String code) {super(message, code);}

    protected AuthenticationException(String message, String detail, String code) {super(message, detail, code);}
}
