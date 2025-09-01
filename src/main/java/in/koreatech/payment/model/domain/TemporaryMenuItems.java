package in.koreatech.payment.model.domain;

import java.util.List;

import in.koreatech.payment.model.entity.Order;
import in.koreatech.payment.model.entity.OrderMenu;
import in.koreatech.payment.model.entity.OrderMenuOption;

public record TemporaryMenuItems(
    String name,
    Integer quantity,
    Integer totalAmount,
    TemporaryMenuPrice price,
    List<TemporaryMenuOption> options
) {
    public OrderMenu toOrderMenu(Order order) {
        OrderMenu orderMenu = OrderMenu.builder()
            .menuName(name)
            .quantity(quantity)
            .menuPrice(price.price())
            .menuPriceName(price.name())
            .order(order)
            .build();

        if (options != null) {
            List<OrderMenuOption> orderMenuOptions = options.stream()
                .map(temporaryMenuOption -> temporaryMenuOption.toOrderMenuOption(orderMenu))
                .toList();
            orderMenu.setOrderMenuOptions(orderMenuOptions);
        }

        return orderMenu;
    }
}
