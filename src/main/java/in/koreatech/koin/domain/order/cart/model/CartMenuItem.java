package in.koreatech.koin.domain.order.cart.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.util.ArrayList;
import java.util.List;

import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenu;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuPrice;
import in.koreatech.payment.common.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "cart_menu_item")
@NoArgsConstructor(access = PROTECTED)
public class CartMenuItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false, unique = true)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderable_shop_menu_id", nullable = false)
    private OrderableShopMenu orderableShopMenu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderable_shop_menu_price_id", nullable = false)
    private OrderableShopMenuPrice orderableShopMenuPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "is_modified", nullable = false)
    private Boolean isModified;

    @OneToMany(mappedBy = "cartMenuItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartMenuItemOption> cartMenuItemOptions = new ArrayList<>();

    public Integer calculateTotalAmount() {
        int totalOptionPrice = this.cartMenuItemOptions.stream()
            .mapToInt(CartMenuItemOption::getOptionPrice)
            .sum();

        return (this.orderableShopMenuPrice.getPrice() + totalOptionPrice) * this.quantity;
    }
}
