package in.koreatech.payment.model.domain;

import java.util.Objects;

import in.koreatech.koin.domain.order.model.OrderMenu;
import in.koreatech.koin.domain.order.model.OrderMenuOption;

public record TemporaryMenuOption(
    String optionGroupName,
    String optionName,
    Integer quantity,
    Integer optionPrice
) {
    public OrderMenuOption toOrderMenuOption(OrderMenu orderMenu) {
        return OrderMenuOption.builder()
            .optionName(Objects.requireNonNullElse(optionName, ""))
            .optionPrice(optionPrice)
            .quantity(quantity)
            .orderMenu(orderMenu)
            .build();
    }
}
