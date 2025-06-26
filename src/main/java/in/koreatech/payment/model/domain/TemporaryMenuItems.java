package in.koreatech.payment.model.domain;

import java.util.List;
import java.util.Objects;

import in.koreatech.koin.domain.order.model.Order;
import in.koreatech.koin.domain.order.model.OrderMenu;
import in.koreatech.koin.domain.order.model.OrderMenuOption;

public record TemporaryMenuItems(
    String name,
    Integer quantity,
    Integer totalAmount,
    TemporaryMenuPrice price,
    List<TemporaryMenuOption> options
) {
    public OrderMenu toOrderMenu(Order order) {
        OrderMenu orderMenu = OrderMenu.builder()
            .menuName(Objects.requireNonNullElse(name, ""))
            .quantity(quantity)
            .menuOptionName(Objects.requireNonNullElse(price.name(), ""))
            .menuPrice(price.price())
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
