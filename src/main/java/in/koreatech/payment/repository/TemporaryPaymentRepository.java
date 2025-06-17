package in.koreatech.payment.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.exception.TemporaryPaymentNotFoundException;
import in.koreatech.payment.model.TemporaryPayment;

public interface TemporaryPaymentRepository extends Repository<TemporaryPayment, String> {

    TemporaryPayment save(TemporaryPayment temporaryPayment);

    Optional<TemporaryPayment> findById(String orderId);

    // TODO. 패키지 정리 이후 커스텀 예외 처리
    default TemporaryPayment getByOrderId(String orderId) {
        return findById(orderId)
            .orElseThrow(() -> TemporaryPaymentNotFoundException.withDetail("orderId: " + orderId));
    }

    void deleteById(String orderId);
}
