package in.koreatech.payment.exception;

import in.koreatech.payment.common.exception.custom.InvalidArgumentException;

public class InvalidOrderIdException extends InvalidArgumentException {

    private static final String DEFAULT_MESSAGE = "orderId는 영문 대소문자, 숫자, 특수문자 '-', '_'로 이루어진 6자 이상 64자 이하의 문자열이어야 합니다.";
    private static final String ERROR_CODE = "INVALID_ORDER_ID";

    public InvalidOrderIdException(String message) {
        super(message, ERROR_CODE);
    }

    public InvalidOrderIdException(String message, String detail) {
        super(message, detail, ERROR_CODE);
    }

    public static InvalidOrderIdException withDetail(String detail) {
        return new InvalidOrderIdException(DEFAULT_MESSAGE, detail);
    }
}
