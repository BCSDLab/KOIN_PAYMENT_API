package in.koreatech.payment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum OrderType {
    DELIVERY("배달"),
    BACKING("포장"),
    ;

    private final String description;

    OrderType(String description) {
        this.description = description;
    }
}
