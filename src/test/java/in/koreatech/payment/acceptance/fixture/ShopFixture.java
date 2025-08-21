package in.koreatech.payment.acceptance.fixture;

import org.springframework.stereotype.Component;

import in.koreatech.koin.domain.order.shop.model.entity.shop.ShopOperation;
import in.koreatech.koin.domain.shop.model.shop.Shop;
import in.koreatech.koin.domain.shop.repository.ShopRepository;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class ShopFixture {

    private final ShopRepository shopRepository;

    public ShopFixture(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public Shop 김밥천국() {
        Shop shop = shopRepository.save(Shop.builder()
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
            .build());

        ShopOperation shopOperation = ShopOperation.builder()
            .isOpen(true)
            .isDeleted(false)
            .shop(shop)
            .build();

        shop.setShopOperation(shopOperation);
        return shop;
    }
}
