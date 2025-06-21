package in.koreatech.payment.model;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    READY,
    IN_PROGRESS,
    WAITING_FOR_DEPOSIT,
    DONE,
    CANCELED,
    PARTIAL_CANCELED,
    ABORTED,
    EXPIRED,
    ;

    public boolean isDone() {
        return this == DONE;
    }
}
