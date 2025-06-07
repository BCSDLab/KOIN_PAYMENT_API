package in.koreatech.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.payment.model.redis.TemporaryPayment;
import in.koreatech.payment.repository.TemporaryPaymentRedisRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TossService implements PaymentService {

    private final TemporaryPaymentRedisRepository temporaryPaymentRedisRepository;

    @Transactional
    public void saveTemporaryPayment(String orderId, Integer userId, Integer amount) {
        TemporaryPayment temporaryPayment = TemporaryPayment.of(orderId, userId, amount);
        temporaryPaymentRedisRepository.save(temporaryPayment);
    }
}
