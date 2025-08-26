package in.koreatech.payment.repository.redis;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.exception.TemporaryPaymentNotFoundException;
import in.koreatech.payment.model.redis.TemporaryPayment;

public interface TemporaryPaymentRedisRepository extends Repository<TemporaryPayment, String> {

    void save(TemporaryPayment temporaryPayment);

    Optional<TemporaryPayment> findById(String pgOrderId);

    default TemporaryPayment getById(String pgOrderId) {
        return findById(pgOrderId)
            .orElseThrow(() -> TemporaryPaymentNotFoundException.withDetail("pgOrderId : " + pgOrderId));
    }

    void deleteById(String pgOrderId);
}
