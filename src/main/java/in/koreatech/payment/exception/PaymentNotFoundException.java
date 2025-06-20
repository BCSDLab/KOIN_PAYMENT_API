package in.koreatech.payment.exception;

import in.koreatech.payment.common.exception.custom.DataNotFoundException;

public class PaymentNotFoundException extends DataNotFoundException {

    private static final String DEFAULT_MESSAGE = "결제 정보가 존재하지 않습니다.";
    private static final String ERROR_CODE = "NOT_FOUND_PAYMENT";

    public PaymentNotFoundException(String message) {
        super(message, ERROR_CODE);
    }

    public PaymentNotFoundException(String message, String detail) {
        super(message, detail, ERROR_CODE);
    }

    public static PaymentNotFoundException withDetail(String detail) {
        return new PaymentNotFoundException(DEFAULT_MESSAGE, detail);
    }
}
