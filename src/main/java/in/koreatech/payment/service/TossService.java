package in.koreatech.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.payment.model.TemporaryPayment;
import in.koreatech.payment.repository.TemporaryPaymentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TossService implements PaymentService {

    private final TemporaryPaymentRepository temporaryPaymentRepository;

    @Transactional
    public void saveTemporaryPayment(String orderId, Integer userId, Integer amount) {
        TemporaryPayment temporaryPayment = TemporaryPayment.of(orderId, userId, amount);
        temporaryPaymentRepository.save(temporaryPayment);
    }
}
