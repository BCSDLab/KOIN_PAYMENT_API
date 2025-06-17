package in.koreatech.koin.domain.user.exception;

import in.koreatech.payment.common.exception.custom.DataNotFoundException;

public class UserNotFoundException extends DataNotFoundException {

    private static final String DEFAULT_MESSAGE = "존재하지 않는 사용자입니다.";
    private static final String code = "NOT_FOUND_USER";

    public UserNotFoundException(String message) {
        super(message, code);
    }

    public UserNotFoundException(String message, String detail) {
        super(message, detail, code);
    }

    public static UserNotFoundException withDetail(String detail) {
        return new UserNotFoundException(DEFAULT_MESSAGE, detail);
    }
}
