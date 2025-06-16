package in.koreatech.payment.common.auth.exception;

import in.koreatech.payment.common.exception.custom.AuthenticationException;

public class UnauthenticatedTokenException extends AuthenticationException {

    private static final String DEFAULT_MESSAGE = "인증되지 않은 엑세스 토큰입니다.";

    public UnauthenticatedTokenException(String message) {
        super(message);
    }

    public UnauthenticatedTokenException(String message, String detail) {
        super(message, detail);
    }

    public static UnauthenticatedTokenException withDetail(String detail) {
        return new UnauthenticatedTokenException(DEFAULT_MESSAGE, detail);
    }
}
