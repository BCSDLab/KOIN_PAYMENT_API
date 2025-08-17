package in.koreatech.payment.acceptance.fixture;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.order.cart.model.Cart;
import in.koreatech.koin.domain.order.cart.repository.CartRepository;
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
}
