package in.koreatech.payment.exception;

import in.koreatech.payment.common.exception.custom.InvalidArgumentException;

public class InvalidTemporaryPaymentException extends InvalidArgumentException {

    private static final String DEFAULT_MESSAGE = "요청한 정보가 임시 결제 정보와 일치하지 않습니다.";
    private static final String code = "MISMATCH_TEMPORARY_PAYMENT";

    public InvalidTemporaryPaymentException(String message) {
        super(message, code);
    }

    public InvalidTemporaryPaymentException(String message, String detail) {
        super(message, detail, code);
    }

    public static InvalidTemporaryPaymentException withDetail(String detail) {
        return new InvalidTemporaryPaymentException(DEFAULT_MESSAGE, detail);
    }
}
