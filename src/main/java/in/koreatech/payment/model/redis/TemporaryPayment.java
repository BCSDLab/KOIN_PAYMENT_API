package in.koreatech.payment.model.redis;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import in.koreatech.payment.exception.InvalidTemporaryPaymentException;
import in.koreatech.payment.model.OrderType;
import in.koreatech.payment.model.TemporaryMenuItems;
import lombok.Getter;

@Getter
@RedisHash(value = "TemporaryPayment@")
public class TemporaryPayment {

    private static final Long CACHE_EXPIRE_SECOND = 60 * 10L;

    @Id
    private String orderId;

    private Integer userId;

    private OrderType orderType;

    private String address;

    private String toOwner;

    private String toRider;

    private Integer totalProductPrice;

    private Integer deliveryFee;

    private Integer totalPrice;

    private List<TemporaryMenuItems> temporaryMenuItems;

    @TimeToLive
    private Long expiryTime;

    private LocalDateTime createdAt;

    private TemporaryPayment(
        String orderId,
        Integer userId,
        OrderType orderType,
        String address,
        String toOwner,
        String toRider,
        Integer totalProductPrice,
        Integer deliveryFee,
        Integer totalPrice,
        List<TemporaryMenuItems> temporaryMenuItems
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderType = orderType;
        this.address = address;
        this.toOwner = toOwner;
        this.toRider = toRider;
        this.totalProductPrice = totalProductPrice;
        this.deliveryFee = deliveryFee;
        this.totalPrice = totalPrice;
        this.temporaryMenuItems = temporaryMenuItems;
        this.expiryTime = CACHE_EXPIRE_SECOND;
        this.createdAt = LocalDateTime.now();
    }

    public static TemporaryPayment toDeliveryEntity(
        String orderId,
        Integer userId,
        String address,
        String toOwner,
        String toRider,
        Integer totalProductPrice,
        Integer deliveryFee,
        Integer totalPrice,
        List<TemporaryMenuItems> temporaryMenuItems
    ) {
        return new TemporaryPayment(
            orderId,
            userId,
            OrderType.DELIVERY,
            address,
            toOwner,
            toRider,
            totalProductPrice,
            deliveryFee,
            totalPrice,
            temporaryMenuItems
        );
    }

    public static TemporaryPayment toTakeOutEntity(
        String orderId,
        Integer userId,
        String toOwner,
        Integer totalProductPrice,
        Integer totalPrice,
        List<TemporaryMenuItems> temporaryMenuItems
    ) {
        return new TemporaryPayment(
            orderId,
            userId,
            OrderType.TAKE_OUT,
            null,
            toOwner,
            null,
            totalProductPrice,
            null,
            totalPrice,
            temporaryMenuItems
        );
    }

    private void validateOrderIdMatches(String orderId) {
        if (!orderId.equals(this.orderId)) {
            throw InvalidTemporaryPaymentException.withDetail("orderId : " + orderId);
        }
    }

    private void validateUserIdMatches(Integer userId) {
        if (!userId.equals(this.userId)) {
            throw InvalidTemporaryPaymentException.withDetail("userId : " + userId);
        }
    }

    private void validateAmountMatches(Integer amount) {
        if (!amount.equals(this.totalProductPrice)) {
            throw InvalidTemporaryPaymentException.withDetail("amount : " + amount);
        }
    }
}
