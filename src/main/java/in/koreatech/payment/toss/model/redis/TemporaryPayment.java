package in.koreatech.payment.toss.model.redis;

import org.hibernate.annotations.Index;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Getter;

@Getter
@RedisHash(value = "TemporaryPayment@")
public class TemporaryPayment {

    private static final Long CACHE_EXPIRE_SECOND = 60 * 10L;
    private static final String ORDER_ID_PATTERN = "^[a-zA-Z0-9-_]{6,64}$";

    @Id
    private String orderId;

    private Integer userId;

    private Integer amount;

    @TimeToLive
    private Long expiryTime;

    public TemporaryPayment(String orderId, Integer userId, Integer amount) {
        validateOrderIdPattern(orderId);
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.expiryTime = CACHE_EXPIRE_SECOND;
    }

    public static TemporaryPayment of(String orderId, Integer userId, Integer amount) {
        return new TemporaryPayment(orderId, userId, amount);
    }

    private void validateOrderIdPattern(String orderId) {
        if (!orderId.matches(ORDER_ID_PATTERN)) {
            throw new IllegalArgumentException("orderId는 영문 대소문자, 숫자, 특수문자 '-', '_'로 이루어진 6자 이상 64자 이하의 문자열이어야 합니다.");
        }
    }
}
