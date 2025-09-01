package in.koreatech.payment.repository.mysql;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.entity.Payment;
import in.koreatech.payment.exception.PaymentNotFoundException;

public interface PaymentRepository extends Repository<Payment, Integer> {

    void save(Payment payment);

    Optional<Payment> findByPaymentKey(String paymentKey);

    default Payment getByPaymentKey(String paymentKey) {
        return findByPaymentKey(paymentKey)
            .orElseThrow(() -> PaymentNotFoundException.withDetail("paymentKey : " + paymentKey));
    }

    Optional<Payment> findById(Integer id);

    default Payment getById(Integer id) {
        return findById(id)
            .orElseThrow(() -> PaymentNotFoundException.withDetail("id : " + id));
    }
}
