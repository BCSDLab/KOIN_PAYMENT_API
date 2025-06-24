package in.koreatech.koin.domain.order.shop.exception;

import in.koreatech.payment.common.exception.custom.DataNotFoundException;

public class OrderableShopNotFoundException extends DataNotFoundException {

    private static final String DEFAULT_MESSAGE = "존재하지 않는 상점입니다.";
    private static final String ERROR_CODE = "ORDERABLE_SHOP_NOT_FOUND";

    public OrderableShopNotFoundException(String message) {
        super(message, ERROR_CODE);
    }

    public OrderableShopNotFoundException(String message, String detail) {
        super(message, detail, ERROR_CODE);
    }

    public static OrderableShopNotFoundException withDetail(String detail) {
        return new OrderableShopNotFoundException(DEFAULT_MESSAGE, detail);
    }
}
