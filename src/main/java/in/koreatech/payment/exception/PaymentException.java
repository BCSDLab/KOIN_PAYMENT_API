package in.koreatech.payment.exception;

import in.koreatech.payment.common.exception.custom.KoinException;

public class PaymentException extends KoinException {

    private static final String DEFAULT_MESSAGE = "결제 시도 중 문제가 생겼습니다.";
    private static final String ERROR_CODE = "PAYMENT_ERROR";

    public PaymentException(String message) {
        super(message, ERROR_CODE);
    }

    public PaymentException(String message, String detail) {
        super(message, detail, ERROR_CODE);
    }

    public static PaymentException withDetail(String detail) {
        return new PaymentException(DEFAULT_MESSAGE, detail);
    }
}
