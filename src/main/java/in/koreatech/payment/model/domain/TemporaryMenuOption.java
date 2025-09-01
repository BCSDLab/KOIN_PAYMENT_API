package in.koreatech.payment.model.domain;

import in.koreatech.payment.model.entity.OrderMenu;
import in.koreatech.payment.model.entity.OrderMenuOption;

public record TemporaryMenuOption(
    String optionGroupName,
    String optionName,
    Integer quantity,
    Integer optionPrice
) {
    public OrderMenuOption toOrderMenuOption(OrderMenu orderMenu) {
        return OrderMenuOption.builder()
            .optionGroupName(optionGroupName)
            .optionName(optionName)
            .optionPrice(optionPrice)
            .quantity(quantity)
            .orderMenu(orderMenu)
            .build();
    }
}
