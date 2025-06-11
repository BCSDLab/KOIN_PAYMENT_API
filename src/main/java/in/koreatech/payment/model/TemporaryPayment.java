package in.koreatech.payment.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.lang.Boolean.FALSE;
import static lombok.AccessLevel.PROTECTED;

import org.hibernate.annotations.Where;

import in.koreatech.payment.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    schema = "koin_payment",
    name = "temporary_payment",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_temporary_payment_order_id", columnNames = "order_id")
    },
    indexes = {
        @Index(name = "idx_temporary_payment_order_id", columnList = "order_id")
    }
)
@Where(clause = "is_deleted=0")
@NoArgsConstructor(access = PROTECTED)
public class TemporaryPayment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @NotNull
    @Size(min = 6, max = 64)
    @Column(name = "order_id", nullable = false, updatable = false, length = 64)
    private String orderId;

    @NotNull
    @Column(name = "user_id", nullable = false, updatable = false)
    private Integer userId;

    @NotNull
    @Column(name = "amount", nullable = false, updatable = false)
    private Integer amount;

    @NotNull
    @Column(name = "is_deleted", nullable = false, columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    private Boolean isDeleted = FALSE;

    private TemporaryPayment(
        String orderId,
        Integer userId,
        Integer amount
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
    }

    public static TemporaryPayment of(
        String orderId,
        Integer userId,
        Integer amount
    ) {
        return new TemporaryPayment(orderId, userId, amount);
    }
}
