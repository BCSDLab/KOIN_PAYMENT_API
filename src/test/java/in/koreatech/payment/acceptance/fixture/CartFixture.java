package in.koreatech.payment.acceptance.fixture;

import java.util.List;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.order.cart.model.Cart;
import in.koreatech.koin.domain.order.cart.repository.CartRepository;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenu;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuOption;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuPrice;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.user.model.User;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class CartFixture {

    private final CartRepository cartRepository;

    public CartFixture(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public Cart 장바구니(User user, OrderableShop orderableShop) {
        return cartRepository.save(Cart.builder()
            .user(user)
            .orderableShop(orderableShop)
            .build()
        );
    }

    public void addOrderMenuItem(
        Cart cart,
        OrderableShopMenu menu,
        OrderableShopMenuPrice price,
        List<OrderableShopMenuOption> options,
        Integer quantity
    ) {
        cart.addItem(menu, price, options, quantity);
        cartRepository.save(cart);
    }
}
