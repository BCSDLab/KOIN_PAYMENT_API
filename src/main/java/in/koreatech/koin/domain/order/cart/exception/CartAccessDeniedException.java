package in.koreatech.koin.domain.order.cart.exception;

import in.koreatech.payment.common.exception.custom.AuthorizationException;

public class CartAccessDeniedException extends AuthorizationException {

    private static final String DEFAULT_MESSAGE = "장바구니 정보 접근 권한이 없습니다.";
    private static final String ERROR_CODE = "CART_ACCESS_DENIED";

    public CartAccessDeniedException(String message) {
        super(message, ERROR_CODE);
    }

    public CartAccessDeniedException(String message, String detail) {
        super(message, detail, ERROR_CODE);
    }

    public static CartAccessDeniedException withDetail(String detail) {
        return new CartAccessDeniedException(DEFAULT_MESSAGE, detail);
    }
}
