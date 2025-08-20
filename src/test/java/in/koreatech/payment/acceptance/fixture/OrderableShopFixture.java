package in.koreatech.payment.acceptance.fixture;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.order.shop.model.entity.shop.ShopOperation;
import in.koreatech.koin.domain.order.shop.repository.OrderableShopRepository;
import in.koreatech.koin.domain.shop.model.shop.Shop;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class OrderableShopFixture {

    private final OrderableShopRepository orderableShopRepository;

    public OrderableShopFixture(OrderableShopRepository orderableShopRepository) {
        this.orderableShopRepository = orderableShopRepository;
    }

    public OrderableShop 김밥천국() {
        ShopOperation shopOperation = ShopOperation.builder()
            .isOpen(true)
            .isDeleted(false)
            .build();

        Shop shop = Shop.builder()
            .name("김밥천국")
            .internalName("김천")
            .chosung("김")
            .phone("010-7574-1212")
            .address("천안시 동남구 병천면 1600")
            .description("김밥천국입니다.")
            .delivery(true)
            .deliveryPrice(3000)
            .payCard(true)
            .payBank(true)
            .isDeleted(false)
            .isEvent(false)
            .remarks("비고")
            .hit(0)
            .bank("국민")
            .accountNumber("01022595923")
            .shopOperation(shopOperation)
            .build();

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
