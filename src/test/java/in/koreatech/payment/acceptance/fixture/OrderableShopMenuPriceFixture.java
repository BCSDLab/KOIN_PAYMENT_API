package in.koreatech.payment.acceptance.fixture;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenu;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuPrice;
import in.koreatech.koin.domain.order.shop.repository.menu.OrderableShopMenuPriceRepository;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class OrderableShopMenuPriceFixture {

    private final OrderableShopMenuPriceRepository orderableShopMenuPriceRepository;

    public OrderableShopMenuPriceFixture(OrderableShopMenuPriceRepository orderableShopMenuPriceRepository) {
        this.orderableShopMenuPriceRepository = orderableShopMenuPriceRepository;
    }

    public OrderableShopMenuPrice 주문_가능_상점_메뉴_가격(OrderableShopMenu menu, String name, Integer price) {
        return orderableShopMenuPriceRepository.save(OrderableShopMenuPrice.builder()
            .price(price)
            .name(name)
            .menu(menu)
            .isDeleted(false)
            .build());
    }
}
