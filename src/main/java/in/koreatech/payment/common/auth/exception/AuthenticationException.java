package in.koreatech.payment.common.auth.exception;

import in.koreatech.payment.common.exception.custom.KoinException;

public abstract class AuthenticationException extends KoinException {

    protected AuthenticationException(String message) {super(message);}

    protected AuthenticationException(String message, String detail) {super(message, detail);}
}
