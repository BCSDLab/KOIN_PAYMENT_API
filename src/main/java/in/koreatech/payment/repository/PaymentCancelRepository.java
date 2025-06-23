package in.koreatech.payment.repository;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.entity.PaymentCancel;

public interface PaymentCancelRepository extends Repository<PaymentCancel, Integer> {

    void saveAll(Iterable<PaymentCancel> paymentCancels);
}
