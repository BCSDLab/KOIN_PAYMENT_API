package in.koreatech.payment.model;

import java.util.List;

public record TemporaryMenuItems (
    String name,
    Integer quantity,
    Integer totalAmount,
    TemporaryMenuPrice price,
    List<TemporaryMenuOption> options
){

}
