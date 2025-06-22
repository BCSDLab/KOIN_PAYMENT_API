package in.koreatech.koin.domain.order.cart.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuOption;
import in.koreatech.payment.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "cart_menu_item_option")
@NoArgsConstructor(access = PROTECTED)
public class CartMenuItemOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_menu_item_id", nullable = false)
    private CartMenuItem cartMenuItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderable_shop_menu_option_id", nullable = false)
    private OrderableShopMenuOption orderableShopMenuOption;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "option_price", nullable = false)
    private Integer optionPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "is_modified", nullable = false)
    private Boolean isModified;
}
