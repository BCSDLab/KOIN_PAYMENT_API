package in.koreatech.payment.toss.model.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.Getter;

@Getter
@RedisHash(value = "TemporaryPayment@")
public class TemporaryPayment {

    private static final Long CACHE_EXPIRE_SECOND = 60 * 10L;

    @Id
    private String orderId;

    private Integer amount;

    @TimeToLive
    private Long expiryTime;

    public TemporaryPayment(String orderId, Integer amount) {
        this.orderId = orderId;
        this.amount = amount;
        this.expiryTime = CACHE_EXPIRE_SECOND;
    }

    public static TemporaryPayment of(String orderId, Integer amount) {
        return new TemporaryPayment(orderId, amount);
    }
}
