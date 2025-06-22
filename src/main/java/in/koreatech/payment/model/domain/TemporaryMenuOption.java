package in.koreatech.payment.model.domain;

public record TemporaryMenuOption(
    String optionGroupName,
    String optionName,
    Integer optionPrice
) {

}
