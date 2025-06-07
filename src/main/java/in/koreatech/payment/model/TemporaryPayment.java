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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "temporary_payment")
@Where(clause = "is_deleted=0")
@NoArgsConstructor(access = PROTECTED)
public class TemporaryPayment extends BaseEntity {

    private static final String ORDER_ID_PATTERN = "^[a-zA-Z0-9-_]{6,64}$";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @NotNull
    @Size(min = 6, max = 64)
    @Column(name = "order_id", nullable = false, updatable = false, unique = true, length = 64)
    private String orderId;

    @NotNull
    @Column(name = "usre_id", nullable = false, updatable = false)
    private Integer userId;

    @NotNull
    @Column(name = "amount", nullable = false, updatable = false)
    private Integer amount;

    @NotNull
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = FALSE;

    public TemporaryPayment(String orderId, Integer userId, Integer amount) {
        validateOrderIdPattern(orderId);
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
    }

    public static TemporaryPayment of(String orderId, Integer userId, Integer amount) {
        return new TemporaryPayment(orderId, userId, amount);
    }

    private void validateOrderIdPattern(String orderId) {
        if (!orderId.matches(ORDER_ID_PATTERN)) {
            throw new IllegalArgumentException("orderId는 영문 대소문자, 숫자, 특수문자 '-', '_'로 이루어진 6자 이상 64자 이하의 문자열이어야 합니다.");
        }
    }
}
