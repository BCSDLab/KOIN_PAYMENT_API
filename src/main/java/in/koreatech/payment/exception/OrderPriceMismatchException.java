package in.koreatech.payment.exception;

import in.koreatech.payment.common.exception.custom.KoinException;

public class OrderPriceMismatchException extends KoinException {

    private static final String DEFAULT_MESSAGE = "클라이언트 요청 금액이 서버 계산 결과와 다릅니다.";
    private static final String ERROR_CODE = "ORDER_PRICE_MISMATCH";

    public OrderPriceMismatchException(String message) {
        super(message, ERROR_CODE);
    }

    public OrderPriceMismatchException(String message, String detail) {
        super(message, detail, ERROR_CODE);
    }

    public static OrderPriceMismatchException withDetail(String detail) {
        return new OrderPriceMismatchException(DEFAULT_MESSAGE, detail);
    }
}
