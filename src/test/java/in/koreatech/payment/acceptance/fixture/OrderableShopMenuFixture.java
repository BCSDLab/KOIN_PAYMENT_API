package in.koreatech.payment.acceptance.fixture;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenu;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.order.shop.repository.menu.OrderableShopMenuRepository;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class OrderableShopMenuFixture {

    private final OrderableShopMenuRepository orderableShopMenuRepository;

    public OrderableShopMenuFixture(OrderableShopMenuRepository orderableShopMenuRepository) {
        this.orderableShopMenuRepository = orderableShopMenuRepository;
    }

    public OrderableShopMenu 주문_가능_상점_메뉴(OrderableShop shop, String name) {
        return orderableShopMenuRepository.save(OrderableShopMenu.builder()
            .orderableShop(shop)
            .name(name)
            .description("메뉴")
            .isSoldOut(false)
            .isDeleted(false)
            .build());
    }
}
