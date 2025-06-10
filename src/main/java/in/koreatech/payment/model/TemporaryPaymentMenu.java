package in.koreatech.payment.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
    schema = "koin_payment",
    name = "temporary_payment_menu"
)
@NoArgsConstructor(access = PROTECTED)
public class TemporaryPaymentMenu {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @NotNull
    @Column(name = "temporary_payment_id", nullable = false, updatable = false)
    private Integer temporaryPaymentId;

    @NotNull
    @Column(name = "menu_id", nullable = false, updatable = false)
    private Integer menuId;

    @NotNull
    @Column(name = "quantity", nullable = false, updatable = false)
    private Integer quantity;

    private TemporaryPaymentMenu(Integer temporaryPaymentId, Integer menuId, Integer quantity) {
        this.temporaryPaymentId = temporaryPaymentId;
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public static TemporaryPaymentMenu of(Integer temporaryPaymentId, Integer menuId, Integer quantity) {
        return new TemporaryPaymentMenu(temporaryPaymentId, menuId, quantity);
    }
}
