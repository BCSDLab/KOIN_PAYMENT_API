package in.koreatech.payment.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import in.koreatech.payment.exception.InvalidOrderIdException;
import in.koreatech.payment.exception.InvalidTemporaryPaymentException;
import lombok.Getter;

@Getter
@RedisHash(value = "TemporaryPayment@")
public class TemporaryPayment {

    private static final String ORDER_ID_PATTERN = "^[a-zA-Z0-9-_]{6,64}$";
    private static final Long CACHE_EXPIRE_SECOND = 60 * 10L;

    @Id
    private String orderId;

    private Integer userId;

    private Integer amount;

    @TimeToLive
    private Long expiryTime;

    private LocalDateTime createdAt;

    private TemporaryPayment(String orderId, Integer userId, Integer amount) {
        validateOrderIdPattern(orderId);
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.expiryTime = CACHE_EXPIRE_SECOND;
        this.createdAt = LocalDateTime.now();
    }

    public static TemporaryPayment of(String orderId, Integer userId, Integer amount) {
        return new TemporaryPayment(orderId, userId, amount);
    }

    private void validateOrderIdPattern(String orderId) {
        if (!orderId.matches(ORDER_ID_PATTERN)) {
            throw InvalidOrderIdException.withDetail("orderId: " + orderId);
        }
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
        if (!amount.equals(this.amount)) {
            throw InvalidTemporaryPaymentException.withDetail("amount : " + amount);
        }
    }
}
