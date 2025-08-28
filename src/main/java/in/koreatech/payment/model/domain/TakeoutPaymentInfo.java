package in.koreatech.payment.model.domain;

import in.koreatech.payment.exception.OrderPriceMismatchException;

public record TakeoutPaymentInfo(
    String phoneNumber,
    String toOwner,
    Boolean provideCutlery,
    Integer totalProductPrice,
    Integer totalAmount
) {
    public static TakeoutPaymentInfo of(
        String phoneNumber, String toOwner, Boolean provideCutlery,
        Integer totalMenuPrice, Integer totalAmount
    ) {
        return new TakeoutPaymentInfo(phoneNumber, toOwner, provideCutlery, totalMenuPrice, totalAmount);
    }
    
    public void validatePrice(Integer totalProductPrice, Integer finalAmount) {
        if (!totalProductPrice().equals(totalProductPrice)
            || !totalAmount().equals(finalAmount)
        ) {
            throw OrderPriceMismatchException.withDetail(
                "totalProductPrice : " + totalProductPrice + "finalAmount : " + finalAmount);
        }
    }
}
