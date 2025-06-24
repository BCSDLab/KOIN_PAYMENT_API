package in.koreatech.koin.domain.order.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import in.koreatech.koin.domain.order.model.Payment;
import in.koreatech.payment.exception.PaymentNotFoundException;

public interface PaymentRepository extends Repository<Payment, Integer> {

    void save(Payment payment);

    Optional<Payment> findByPaymentKey(String paymentKey);

    default Payment getByPaymentKey(String paymentKey) {
        return findByPaymentKey(paymentKey)
            .orElseThrow(() -> PaymentNotFoundException.withDetail("paymentKey : " + paymentKey));
    }
}
