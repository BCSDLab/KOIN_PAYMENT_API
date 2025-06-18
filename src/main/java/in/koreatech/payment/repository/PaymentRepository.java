package in.koreatech.payment.repository;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.Payment;

public interface PaymentRepository extends Repository<Payment, Integer> {

    void save(Payment payment);
}
