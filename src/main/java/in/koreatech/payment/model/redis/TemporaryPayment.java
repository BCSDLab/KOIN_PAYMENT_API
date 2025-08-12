package in.koreatech.payment.model.redis;

import static in.koreatech.koin.domain.order.model.OrderType.DELIVERY;
import static in.koreatech.koin.domain.order.model.OrderType.TAKE_OUT;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import in.koreatech.koin.domain.order.model.Order;
import in.koreatech.koin.domain.order.model.OrderDelivery;
import in.koreatech.koin.domain.order.model.OrderTakeout;
import in.koreatech.koin.domain.order.model.OrderType;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.payment.exception.InvalidTemporaryPaymentException;
import in.koreatech.payment.model.domain.TemporaryMenuItems;
import lombok.Getter;

@Getter
@RedisHash(value = "TemporaryPayment@")
public class TemporaryPayment {

    private static final Long CACHE_EXPIRE_SECOND = 60 * 10L;

    @Id
    private String orderId;

    private Integer userId;

    private Integer orderableShopId;

    private String phoneNumber;

    private OrderType orderType;

    private String address;

    private String toOwner;

    private String toRider;

    private Boolean provideCutlery;

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
        Integer orderableShopId,
        String phoneNumber,
        OrderType orderType,
        String address,
        String toOwner,
        String toRider,
        Boolean provideCutlery,
        Integer totalProductPrice,
        Integer deliveryFee,
        Integer totalPrice,
        List<TemporaryMenuItems> temporaryMenuItems
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderableShopId = orderableShopId;
        this.phoneNumber = phoneNumber;
        this.orderType = orderType;
        this.address = address;
        this.toOwner = toOwner;
        this.toRider = toRider;
        this.provideCutlery = provideCutlery;
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
        Integer orderableShopId,
        String phoneNumber,
        String address,
        String toOwner,
        String toRider,
        Boolean provideCutlery,
        Integer totalProductPrice,
        Integer deliveryFee,
        Integer totalPrice,
        List<TemporaryMenuItems> temporaryMenuItems
    ) {
        return new TemporaryPayment(
            orderId,
            userId,
            orderableShopId,
            phoneNumber,
            OrderType.DELIVERY,
            address,
            toOwner,
            toRider,
            provideCutlery,
            totalProductPrice,
            deliveryFee,
            totalPrice,
            temporaryMenuItems
        );
    }

    public static TemporaryPayment toTakeOutEntity(
        String orderId,
        Integer userId,
        Integer orderableShopId,
        String phoneNumber,
        String toOwner,
        Boolean provideCutlery,
        Integer totalProductPrice,
        Integer totalPrice,
        List<TemporaryMenuItems> temporaryMenuItems
    ) {
        return new TemporaryPayment(
            orderId,
            userId,
            orderableShopId,
            phoneNumber,
            OrderType.TAKE_OUT,
            null,
            toOwner,
            null,
            provideCutlery,
            totalProductPrice,
            null,
            totalPrice,
            temporaryMenuItems
        );
    }

    public Order toOrder(User user, OrderableShop orderableShop) {
        Order order = Order.builder()
            .id(orderId)
            .orderType(orderType)
            .phoneNumber(phoneNumber)
            .totalProductPrice(totalProductPrice)
            .totalPrice(totalPrice)
            .orderableShop(orderableShop)
            .user(user)
            .isDeleted(false)
            .build();

        if (orderType == DELIVERY) {
            order.setOrderDelivery(OrderDelivery.builder()
                .order(order)
                .address(address)
                .toOwner(toOwner)
                .toRider(toRider)
                .provideCutlery(provideCutlery)
                .deliveryTip(deliveryFee)
                .build());
        } else if (orderType == TAKE_OUT) {
            order.setOrderTakeout(OrderTakeout.builder()
                .order(order)
                .toOwner(toOwner)
                .provideCutlery(provideCutlery)
                .build());
        }

        return order;
    }

    public void validateMatches(String orderId, Integer userId, Integer amount) {
        validateOrderIdMatches(orderId);
        validateUserIdMatches(userId);
        validateAmountMatches(amount);
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
        if (!amount.equals(this.totalPrice)) {
            throw InvalidTemporaryPaymentException.withDetail("amount : " + amount);
        }
    }
}
