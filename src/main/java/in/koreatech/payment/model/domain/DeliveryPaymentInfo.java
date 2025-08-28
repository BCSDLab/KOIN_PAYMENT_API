package in.koreatech.payment.model.domain;

import in.koreatech.payment.exception.OrderPriceMismatchException;

public record DeliveryPaymentInfo(
    String phoneNumber,
    String address,
    String toOwner,
    String toRider,
    Boolean provideCutlery,
    Integer totalMenuPrice,
    Integer deliveryTip,
    Integer totalAmount
) {
    public static DeliveryPaymentInfo of(
        String phoneNumber,
        String address,
        String toOwner,
        String toRider,
        Boolean provideCutlery,
        Integer totalMenuPrice,
        Integer deliveryTip,
        Integer totalAmount
    ) {
        return new DeliveryPaymentInfo(
            phoneNumber,
            address,
            toOwner,
            toRider,
            provideCutlery,
            totalMenuPrice,
            deliveryTip,
            totalAmount
        );
    }
    
    public void validatePrice(Integer totalProductPrice, Integer deliveryFee, Integer finalAmount) {
        if (!totalMenuPrice().equals(totalProductPrice)
            || !deliveryTip().equals(deliveryFee)
            || !totalAmount().equals(finalAmount)
        ) {
            throw OrderPriceMismatchException.withDetail(
                "totalProductPrice : " + totalProductPrice + "deliveryFee : " + deliveryFee + "totalAmount : "
                    + totalProductPrice + "finalAmount : " + finalAmount);
        }
    }
}
