package in.koreatech.payment.exception;

import in.koreatech.payment.common.exception.custom.DataNotFoundException;

public class TemporaryPaymentNotFoundException extends DataNotFoundException {

    private static final String DEFAULT_MESSAGE = "임시 결제 정보가 존재하지 않습니다.";
    private static final String CODE = "NOT_FOUND_TEMPORARY_PAYMENT";

    public TemporaryPaymentNotFoundException(String message) {
        super(message, CODE);
    }

    public TemporaryPaymentNotFoundException(String message, String detail) {
        super(message, detail, CODE);
    }

    public static TemporaryPaymentNotFoundException withDetail(String detail) {
        return new TemporaryPaymentNotFoundException(DEFAULT_MESSAGE, detail);
    }
}
