package in.koreatech.koin.domain.order.cart.exception;

import in.koreatech.payment.common.exception.custom.DataNotFoundException;

public class CartNotFoundException extends DataNotFoundException {

    private static final String DEFAULT_MESSAGE = "장바구니가 존재하지 않습니다.";
    private static final String ERROR_CODE = "NOT_FOUND_CART";

    public CartNotFoundException(String message) {
        super(message, ERROR_CODE);
    }

    public CartNotFoundException(String message, String detail) {
        super(message, detail, ERROR_CODE);
    }

    public static CartNotFoundException withDetail(String detail) {
        return new CartNotFoundException(DEFAULT_MESSAGE, detail);
    }
}
