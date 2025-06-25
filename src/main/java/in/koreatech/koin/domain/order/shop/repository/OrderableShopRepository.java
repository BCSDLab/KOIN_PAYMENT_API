package in.koreatech.koin.domain.order.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.koreatech.koin.domain.order.shop.exception.OrderableShopNotFoundException;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;

public interface OrderableShopRepository extends JpaRepository<OrderableShop, Integer> {
    default OrderableShop getById(Integer shopId) {
        return findById(shopId)
            .orElseThrow(() -> new OrderableShopNotFoundException("해당 상점이 존재하지 않습니다 : " + shopId));
    }
}
