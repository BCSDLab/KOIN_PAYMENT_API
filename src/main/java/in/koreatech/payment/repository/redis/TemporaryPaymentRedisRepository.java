package in.koreatech.payment.repository.redis;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.exception.TemporaryPaymentNotFoundException;
import in.koreatech.payment.model.redis.TemporaryPayment;

public interface TemporaryPaymentRedisRepository extends Repository<TemporaryPayment, String> {

    void save(TemporaryPayment temporaryPayment);

    Optional<TemporaryPayment> findById(String orderId);

    default TemporaryPayment getById(String orderId) {
        return findById(orderId)
            .orElseThrow(() -> TemporaryPaymentNotFoundException.withDetail("orderId : " + orderId));
    }

    void deleteById(String orderId);
}
