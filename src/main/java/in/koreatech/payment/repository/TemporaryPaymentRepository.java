package in.koreatech.payment.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import in.koreatech.payment.model.TemporaryPayment;

public interface TemporaryPaymentRepository extends Repository<TemporaryPayment, Integer> {

    TemporaryPayment save(TemporaryPayment temporaryPayment);

    Optional<TemporaryPayment> findByOrderId(String orderId);

    // TODO. 패키지 정리 이후 커스텀 예외 처리
    default TemporaryPayment getByOrderId(String orderId) {
        return findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("TemporaryPayment not found"));
    }
}
