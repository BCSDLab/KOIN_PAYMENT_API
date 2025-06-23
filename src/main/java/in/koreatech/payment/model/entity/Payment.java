package in.koreatech.payment.model.entity;

import static in.koreatech.payment.model.enums.PaymentStatus.CANCELED;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.lang.Boolean.FALSE;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;

import org.hibernate.annotations.Where;

import in.koreatech.payment.exception.PaymentAccessDeniedException;
import in.koreatech.payment.model.enums.PaymentMethod;
import in.koreatech.payment.model.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "payment")
@Where(clause = "is_deleted=0")
@NoArgsConstructor(access = PROTECTED)
public class Payment {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 200)
    @Column(name = "payment_key", length = 200, nullable = false, updatable = false)
    private String paymentKey;

    @NotBlank
    @Size(min = 6, max = 64)
    @Column(name = "order_id", length = 64, nullable = false, updatable = false)
    private String orderId;

    @NotNull
    @Column(name = "amount", nullable = false, updatable = false)
    private Integer amount;

    @NotNull
    @Column(name = "user_id", nullable = false, updatable = false)
    private Integer userId;

    @NotNull
    @Size(max = 30)
    @Enumerated(STRING)
    @Column(name = "status", length = 30, nullable = false)
    private PaymentStatus paymentStatus;

    @NotNull
    @Size(max = 30)
    @Enumerated(STRING)
    @Column(name = "method", length = 30, nullable = false, updatable = false)
    private PaymentMethod paymentMethod;

    @NotNull
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @NotNull
    @Column(name = "approved_at", nullable = false, updatable = false)
    private LocalDateTime approvedAt;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = FALSE;

    @Builder
    private Payment(
        String paymentKey,
        String orderId,
        Integer amount,
        Integer userId,
        PaymentStatus paymentStatus,
        PaymentMethod paymentMethod,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt
    ) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.userId = userId;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }


    public void validateUserIdMatches(Integer userId) {
        if (!userId.equals(this.userId)) {
            throw PaymentAccessDeniedException.withDetail("userId : " + userId);
        }
    }

    public void cancel() {
        this.paymentStatus = CANCELED;
    }
}
