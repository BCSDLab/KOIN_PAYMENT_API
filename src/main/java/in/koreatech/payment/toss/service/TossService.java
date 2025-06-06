package in.koreatech.payment.toss.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.payment.toss.dto.request.TemporaryPaymentSaveRequest;
import in.koreatech.payment.toss.model.redis.TemporaryPayment;
import in.koreatech.payment.toss.repository.TemporaryPaymentRedisRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TossService {

    private final TemporaryPaymentRedisRepository temporaryPaymentRedisRepository;

    @Transactional
    public void saveTemporaryPayment(TemporaryPaymentSaveRequest request) {
        TemporaryPayment temporaryPayment = TemporaryPayment.of(request.orderId(), request.amount());
        temporaryPaymentRedisRepository.save(temporaryPayment);
    }
}
