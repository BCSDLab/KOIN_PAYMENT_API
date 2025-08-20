package in.koreatech.payment.acceptance.fixture;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenu;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuPrice;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class OrderShopMenuPriceFixture {

    public OrderableShopMenuPrice 주문_가능_상점_메뉴_가격(OrderableShopMenu menu, String name, Integer price) {
        return OrderableShopMenuPrice.builder()
            .price(price)
            .name(name)
            .menu(menu)
            .isDeleted(false)
            .build();
    }
}
