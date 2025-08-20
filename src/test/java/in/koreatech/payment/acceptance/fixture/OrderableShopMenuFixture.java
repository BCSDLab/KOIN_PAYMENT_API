package in.koreatech.payment.acceptance.fixture;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenu;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class OrderableShopMenuFixture {

    public OrderableShopMenu 주문_가능_상점_메뉴(OrderableShop shop, String name) {
        return OrderableShopMenu.builder()
            .orderableShop(shop)
            .name(name)
            .description("메뉴")
            .isSoldOut(false)
            .isDeleted(false)
            .build();
    }
}
