package in.koreatech.payment.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static java.lang.Boolean.FALSE;
import static lombok.AccessLevel.PROTECTED;

import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = FALSE;

    @Builder
    private Payment(
        String paymentKey,
        String orderId,
        Integer amount,
        Integer userId
    ) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.userId = userId;
    }
}
