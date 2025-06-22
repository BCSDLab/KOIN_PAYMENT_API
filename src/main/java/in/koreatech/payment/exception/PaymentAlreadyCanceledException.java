package in.koreatech.payment.exception;

import in.koreatech.payment.common.exception.custom.InvalidArgumentException;

public class PaymentAlreadyCanceledException extends InvalidArgumentException {

    private static final String DEFAULT_MESSAGE = "이미 취소된 결제입니다.";
    private static final String ERROR_CODE = "PAYMENT_ALREADY_CANCELED";

    public PaymentAlreadyCanceledException(String message) {
        super(message, ERROR_CODE);
    }

    public PaymentAlreadyCanceledException(String message, String detail) {
        super(message, detail, ERROR_CODE);
    }

    public static PaymentAlreadyCanceledException withDetail(String detail) {
        return new PaymentAlreadyCanceledException(DEFAULT_MESSAGE, detail);
    }
}
