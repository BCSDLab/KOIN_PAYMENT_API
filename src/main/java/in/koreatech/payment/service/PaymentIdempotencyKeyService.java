package in.koreatech.payment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import in.koreatech.payment.model.entity.PaymentIdempotencyKey;
import in.koreatech.payment.repository.mysql.PaymentIdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentIdempotencyKeyService {

    private final PaymentIdempotencyKeyRepository paymentIdempotencyKeyRepository;

    public String getOrCreate(Integer userId) {
        return paymentIdempotencyKeyRepository
            .findByUserId(userId)
            .map(idempotencyKey -> {
                if (idempotencyKey.isOlderThanExpireDays()) {
                    idempotencyKey.updateIdempotencyKey(UUID.randomUUID().toString());
                }
                return idempotencyKey;
            })
            .orElseGet(() -> paymentIdempotencyKeyRepository.save(
                PaymentIdempotencyKey.builder()
                    .userId(userId)
                    .idempotencyKey(UUID.randomUUID().toString())
                    .build()
            )).getIdempotencyKey();
    }
}
