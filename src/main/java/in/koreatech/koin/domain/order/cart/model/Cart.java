package in.koreatech.koin.domain.order.cart.model;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenu;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuOption;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuPrice;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.user.model.User;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "cart")
@NoArgsConstructor(access = PROTECTED)
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderable_shop_id", nullable = false)
    private OrderableShop orderableShop;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    List<CartMenuItem> cartMenuItems = new ArrayList<>();

    @Builder
    private Cart(
        User user,
        OrderableShop orderableShop
    ) {
        this.user = user;
        this.orderableShop = orderableShop;
    }

    public Integer calculateItemsAmount() {
        return this.cartMenuItems.stream()
            .mapToInt(CartMenuItem::calculateTotalAmount)
            .sum();
    }

    // TODO. 코인 서버와 동일 로직으로 변경
    public void addItem(OrderableShopMenu menu, OrderableShopMenuPrice price, List<OrderableShopMenuOption> options,
        Integer quantity) {
        // 장바구니에 옵션 까지 전부 동일한 메뉴가 이미 존재 하는 경우, 담긴 상품 수량 증가
        Optional<CartMenuItem> existingItem = findSameItem(menu, price, options);

        if (existingItem.isPresent()) {
            existingItem.get().increaseQuantity(quantity);
        } else {
            this.cartMenuItems.add(CartMenuItem.builder()
                .cart(this)
                .orderableShopMenu(menu)
                .orderableShopMenuPrice(price)
                .quantity(quantity)
                .isModified(false)
                .build()
            );
        }
    }

    private Optional<CartMenuItem> findSameItem(OrderableShopMenu menu, OrderableShopMenuPrice price,
        List<OrderableShopMenuOption> options) {
        return this.cartMenuItems.stream()
            .filter(item -> item.isSameItem(menu, price, options))
            .findFirst();
    }
}
