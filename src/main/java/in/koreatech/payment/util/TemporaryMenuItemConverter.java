package in.koreatech.payment.util;

import java.util.List;

import in.koreatech.koin.domain.order.cart.model.Cart;
import in.koreatech.koin.domain.order.cart.model.CartMenuItem;
import in.koreatech.koin.domain.order.cart.model.CartMenuItemOption;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuOption;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuPrice;
import in.koreatech.payment.model.domain.TemporaryMenuItems;
import in.koreatech.payment.model.domain.TemporaryMenuOption;
import in.koreatech.payment.model.domain.TemporaryMenuPrice;

public class TemporaryMenuItemConverter {

    private TemporaryMenuItemConverter() {
    }

    public static List<TemporaryMenuItems> fromCart(Cart cart) {
        return cart.getCartMenuItems().stream()
            .map(TemporaryMenuItemConverter::fromCartMenuItem)
            .toList();
    }

    public static TemporaryMenuItems fromCartMenuItem(CartMenuItem cartMenuItem) {
        List<TemporaryMenuOption> options = cartMenuItem.getCartMenuItemOptions().stream()
            .map(TemporaryMenuItemConverter::fromCartMenuItemOption)
            .toList();

        OrderableShopMenuPrice price = cartMenuItem.getOrderableShopMenuPrice();

        return new TemporaryMenuItems(
            cartMenuItem.getOrderableShopMenu().getName(),
            cartMenuItem.getQuantity(),
            cartMenuItem.calculateTotalAmount(),
            new TemporaryMenuPrice(price.getName(), price.getPrice()),
            options
        );
    }

    private static TemporaryMenuOption fromCartMenuItemOption(CartMenuItemOption option) {
        OrderableShopMenuOption shopOption = option.getOrderableShopMenuOption();
        String optionGroupName = shopOption.getOptionGroup().getName();

        return new TemporaryMenuOption(
            optionGroupName,
            option.getOptionName(),
            option.getQuantity(),
            option.getOptionPrice()
        );
    }
}
