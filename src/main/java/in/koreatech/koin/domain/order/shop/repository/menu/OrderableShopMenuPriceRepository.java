package in.koreatech.koin.domain.order.shop.repository.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuPrice;

public interface OrderableShopMenuPriceRepository extends JpaRepository<OrderableShopMenuPrice, Integer> {

}
