package in.koreatech.payment.common.exception.custom;

public class KoinIllegalStateException extends KoinException {

    private static final String DEFAULT_MESSAGE = "서버에 문제가 생겼습니다.";
    private static final String CODE = "KOIN_SERVER_ERROR";

    public KoinIllegalStateException(String message) {
        super(message, CODE);
    }

    public KoinIllegalStateException(String message, String detail) {
        super(message, detail, CODE);
    }

    public static KoinIllegalStateException withDetail(String detail) {
        return new KoinIllegalStateException(DEFAULT_MESSAGE, detail);
    }
}
