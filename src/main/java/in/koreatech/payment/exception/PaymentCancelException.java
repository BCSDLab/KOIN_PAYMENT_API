package in.koreatech.payment.exception;

import in.koreatech.payment.common.exception.custom.ExternalServiceException;

public class PaymentCancelException extends ExternalServiceException {

    private static final String DEFAULT_MESSAGE = "결제 취소 중 문제가 생겼습니다.";
    private static final String ERROR_CODE = "PAYMENT_CANCEL_ERROR";

    public PaymentCancelException(String message) {
        super(message, ERROR_CODE);
    }

    public PaymentCancelException(String message, String detail) {
        super(message, detail, ERROR_CODE);
    }

    public static PaymentConfirmException withDetail(String detail) {
        return new PaymentConfirmException(DEFAULT_MESSAGE, detail);
    }
}
