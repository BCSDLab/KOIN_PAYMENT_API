package in.koreatech.payment.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import in.koreatech.payment.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    name = "payment_idempotency_key",
    uniqueConstraints = @UniqueConstraint(name = "uk_idempotency_key_user_id", columnNames = "user_id")
)
@NoArgsConstructor(access = PROTECTED)
public class PaymentIdempotencyKey extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "user_id", nullable = false, updatable = false)
    private Integer userId;

    @NotNull
    @Size(max = 300)
    @Column(name = "idempotency_key", nullable = false, length = 300)
    private String idempotencyKey;

    @Builder
    private PaymentIdempotencyKey(
        Integer userId,
        String idempotencyKey
    ) {
        this.userId = userId;
        this.idempotencyKey = idempotencyKey;
    }
}
