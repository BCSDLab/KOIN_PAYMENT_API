package in.koreatech.payment.repository;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.redis.TemporaryPayment;

public interface TemporaryPaymentRedisRepository extends Repository<TemporaryPayment, String> {

    void save(TemporaryPayment temporaryPayment);
}
