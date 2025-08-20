package in.koreatech.payment.acceptance.fixture;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.order.shop.repository.OrderableShopRepository;
import in.koreatech.koin.domain.shop.model.shop.Shop;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class OrderableShopFixture {

    private final OrderableShopRepository orderableShopRepository;

    public OrderableShopFixture(OrderableShopRepository orderableShopRepository) {
        this.orderableShopRepository = orderableShopRepository;
    }

    public OrderableShop 주문_가능_김밥천국(Shop shop) {
        return orderableShopRepository.save(OrderableShop.builder()
            .shop(shop)
            .minimumOrderAmount(15000)
            .takeout(true)
            .delivery(true)
            .serviceEvent(false)
            .isDeleted(false)
            .build());
    }
}
