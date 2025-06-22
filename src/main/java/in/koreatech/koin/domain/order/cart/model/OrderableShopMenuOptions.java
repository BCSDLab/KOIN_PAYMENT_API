package in.koreatech.koin.domain.order.cart.model;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuOption;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderableShopMenuOptions {

    private List<OrderableShopMenuOption> options;
}
