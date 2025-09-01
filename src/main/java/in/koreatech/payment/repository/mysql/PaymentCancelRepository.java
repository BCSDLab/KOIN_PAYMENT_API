package in.koreatech.payment.repository.mysql;

import java.util.List;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.entity.PaymentCancel;

public interface PaymentCancelRepository extends Repository<PaymentCancel, Integer> {

    void saveAll(Iterable<PaymentCancel> paymentCancels);

    List<PaymentCancel> findAllByPaymentId(Integer paymentId);
}
