package in.koreatech.payment.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.exception.PaymentNotFoundException;
import in.koreatech.payment.model.Payment;

public interface PaymentRepository extends Repository<Payment, Integer> {

    void save(Payment payment);

    Optional<Payment> findByPaymentKey(String paymentKey);

    default Payment getByPaymentKey(String paymentKey) {
        return findByPaymentKey(paymentKey)
            .orElseThrow(() -> PaymentNotFoundException.withDetail("paymentKey : " + paymentKey));
    }
}
