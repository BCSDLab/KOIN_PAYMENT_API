package in.koreatech.payment.exception;

import in.koreatech.payment.common.exception.custom.AuthorizationException;

public class PaymentAccessDeniedException extends AuthorizationException {

    private static final String DEFAULT_MESSAGE = "결제 정보 접근 권한이 없습니다.";
    private static final String ERROR_CODE = "PAYMENT_ACCESS_DENIED";

    public PaymentAccessDeniedException(String message) {
        super(message, ERROR_CODE);
    }

    public PaymentAccessDeniedException(String message, String detail) {
        super(message, detail, ERROR_CODE);
    }

    public static PaymentAccessDeniedException withDetail(String detail) {
        return new PaymentAccessDeniedException(DEFAULT_MESSAGE, detail);
    }
}
