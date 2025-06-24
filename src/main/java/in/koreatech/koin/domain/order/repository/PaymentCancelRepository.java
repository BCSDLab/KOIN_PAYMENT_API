package in.koreatech.koin.domain.order.repository;

import org.springframework.data.repository.Repository;

import in.koreatech.koin.domain.order.model.PaymentCancel;

public interface PaymentCancelRepository extends Repository<PaymentCancel, Integer> {

    void saveAll(Iterable<PaymentCancel> paymentCancels);
}
