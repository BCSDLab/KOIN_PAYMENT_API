package in.koreatech.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.koin.domain.user.model.User;
import in.koreatech.koin.domain.user.repository.UserRepository;
import in.koreatech.payment.common.auth.JwtProvider;
import in.koreatech.payment.dto.request.TemporaryDeliveryPaymentSaveRequest;
import in.koreatech.payment.dto.request.TemporaryTakeoutPaymentSaveRequest;
import in.koreatech.payment.dto.response.PaymentCancelResponse;
import in.koreatech.payment.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.dto.response.PaymentResponse;
import in.koreatech.payment.dto.response.TemporaryPaymentResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final TemporaryPaymentService temporaryPaymentService;
    private final PaymentConfirmService paymentConfirmService;
    private final PaymentCancelService paymentCancelService;
    private final PaymentQueryService paymentQueryService;

    @Transactional
    public TemporaryPaymentResponse createTemporaryDeliveryPayment(String accessToken, TemporaryDeliveryPaymentSaveRequest request) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);
        return temporaryPaymentService.createDeliveryPayment(user, request);
    }

    @Transactional
    public TemporaryPaymentResponse createTemporaryTakeoutPayment(String accessToken, TemporaryTakeoutPaymentSaveRequest request) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);
        return temporaryPaymentService.createTakeoutPayment(user, request);
    }

    @Transactional
    public PaymentConfirmResponse confirmPayment(String accessToken, String paymentKey, String orderId, Integer amount) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);
        return paymentConfirmService.confirmPayment(user, paymentKey, orderId, amount);
    }

    @Transactional
    public PaymentCancelResponse cancelPayment(String accessToken, Integer paymentId, String cancelReason) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);
        return paymentCancelService.cancelPayment(user, paymentId, cancelReason);
    }

    public PaymentResponse getPayment(String accessToken, Integer paymentId) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);
        return paymentQueryService.getPayment(user, paymentId);
    }
}
