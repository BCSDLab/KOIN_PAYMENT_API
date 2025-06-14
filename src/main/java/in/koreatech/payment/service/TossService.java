package in.koreatech.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.koin.domain.user.model.User;
import in.koreatech.koin.domain.user.repository.UserRepository;
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
    private final JwtTokenResolver jwtTokenResolver;
    private final UserRepository userRepository;

    @Transactional
    public String createTemporaryPayment(String accessToken, Integer amount) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        User user = userRepository.getById(userId);
        String orderId = orderIdGenerator.generateOrderId();
        TemporaryPayment temporaryPayment = temporaryPaymentRepository.save(TemporaryPayment.of(orderId, user.getId(), amount));
        return temporaryPayment.getOrderId();
    }

    @Transactional
    public void confirmPayment(String accessToken, String paymentKey, String orderId, Integer amount) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        User user = userRepository.getById(userId);
        TemporaryPayment temporaryPayment = temporaryPaymentRepository.getByOrderId(orderId);
        temporaryPayment.validateMatches(paymentKey, user.getId(), amount);
    }
}
