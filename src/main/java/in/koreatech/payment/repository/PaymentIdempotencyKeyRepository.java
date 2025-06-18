package in.koreatech.payment.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.PaymentIdempotencyKey;

public interface PaymentIdempotencyKeyRepository extends Repository<PaymentIdempotencyKey, Integer> {

    PaymentIdempotencyKey save(PaymentIdempotencyKey paymentIdempotencyKey);

    Optional<PaymentIdempotencyKey> findByUserId(Integer userId);
}
