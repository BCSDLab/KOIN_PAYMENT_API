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

    public boolean isCanceled() {
        return this == CANCELED || this == PARTIAL_CANCELED;
    }

    public boolean isFailed() {
        return this == ABORTED || this == EXPIRED;
    }

    public boolean isInProgress() {
        return this == IN_PROGRESS || this == WAITING_FOR_DEPOSIT;
    }
}
