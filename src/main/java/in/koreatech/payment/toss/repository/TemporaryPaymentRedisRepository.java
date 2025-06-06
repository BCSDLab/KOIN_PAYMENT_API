package in.koreatech.payment.toss.repository;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.toss.model.redis.TemporaryPayment;

public interface TemporaryPaymentRedisRepository extends Repository<TemporaryPayment, String> {

    TemporaryPayment save(TemporaryPayment temporaryPayment);
}
