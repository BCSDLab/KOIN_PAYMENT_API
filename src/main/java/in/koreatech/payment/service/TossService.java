package in.koreatech.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.payment.client.KoinClient;
import in.koreatech.payment.common.auth.JwtTokenResolver;
import in.koreatech.payment.model.TemporaryPayment;
import in.koreatech.payment.repository.TemporaryPaymentRepository;
import in.koreatech.payment.util.OrderIdGenerator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TossService implements PaymentService {

    private final TemporaryPaymentRepository temporaryPaymentRepository;
    private final OrderIdGenerator orderIdGenerator;
    private final KoinClient koinClient;
    private final JwtTokenResolver jwtTokenResolver;

    @Transactional
    public String createTemporaryPayment(String accessToken, Integer amount) {
        koinClient.checkLogin(accessToken).block();
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        String orderId = orderIdGenerator.generateOrderId();
        TemporaryPayment temporaryPayment = temporaryPaymentRepository.save(TemporaryPayment.of(orderId, userId, amount));
        return temporaryPayment.getOrderId();
    }
}
