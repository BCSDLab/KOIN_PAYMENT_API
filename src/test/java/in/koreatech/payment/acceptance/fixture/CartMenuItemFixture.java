package in.koreatech.payment.acceptance.fixture;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.order.cart.model.Cart;
import in.koreatech.koin.domain.order.cart.model.CartMenuItem;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenu;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuPrice;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class CartMenuItemFixture {

    public static CartMenuItem 옵션이_없는_장바구니_메뉴(
        Cart cart, OrderableShopMenu menu, OrderableShopMenuPrice menuPrice, Integer quantity
    ) {
        return CartMenuItem.builder()
            .cart(cart)
            .orderableShopMenu(menu)
            .orderableShopMenuPrice(menuPrice)
            .quantity(quantity)
            .isModified(false)
            .build();
    }
}
