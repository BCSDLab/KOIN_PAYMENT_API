package in.koreatech.koin.domain.order.cart.model;

import java.util.List;

import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderableShopMenus {

    private Integer orderableShopId;
    private List<OrderableShopMenu> menuList;
}
