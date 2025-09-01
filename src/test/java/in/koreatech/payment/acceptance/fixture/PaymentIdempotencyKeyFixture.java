package in.koreatech.payment.acceptance.fixture;

import org.springframework.stereotype.Component;

import in.koreatech.payment.model.entity.PaymentIdempotencyKey;
import in.koreatech.payment.repository.mysql.PaymentIdempotencyKeyRepository;
import in.koreatech.koin.domain.user.model.User;

@Component
@SuppressWarnings("NonAsciiCharacters")
public class PaymentIdempotencyKeyFixture {

    private final PaymentIdempotencyKeyRepository paymentIdempotencyKeyRepository;

    public PaymentIdempotencyKeyFixture(PaymentIdempotencyKeyRepository paymentIdempotencyKeyRepository) {
        this.paymentIdempotencyKeyRepository = paymentIdempotencyKeyRepository;
    }

    public PaymentIdempotencyKey 결제_멱등_키(User user, String idempotencyKey) {
        return paymentIdempotencyKeyRepository.save(PaymentIdempotencyKey.builder()
            .userId(user.getId())
            .idempotencyKey(idempotencyKey)
            .build()
        );
    }
}
