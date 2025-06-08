package in.koreatech.payment.repository;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.TemporaryPayment;

public interface TemporaryPaymentRepository extends Repository<TemporaryPayment, Integer> {

    void save(TemporaryPayment temporaryPayment);
}
