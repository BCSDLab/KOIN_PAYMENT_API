package in.koreatech.koin.domain.order.shop.repository.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenu;

public interface OrderableShopMenuRepository extends JpaRepository<OrderableShopMenu, Integer> {
}
