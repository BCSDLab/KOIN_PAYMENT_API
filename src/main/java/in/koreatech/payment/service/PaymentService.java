package in.koreatech.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.koin.domain.user.model.User;
import in.koreatech.payment.dto.request.PaymentCancelRequest;
import in.koreatech.payment.dto.request.PaymentConfirmRequest;
import in.koreatech.payment.dto.request.TemporaryDeliveryPaymentSaveRequest;
import in.koreatech.payment.dto.request.TemporaryTakeoutPaymentSaveRequest;
import in.koreatech.payment.dto.response.PaymentCancelResponse;
import in.koreatech.payment.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.dto.response.PaymentResponse;
import in.koreatech.payment.dto.response.TemporaryPaymentResponse;
import in.koreatech.payment.model.domain.DeliveryPaymentInfo;
import in.koreatech.payment.model.domain.PaymentCancelInfo;
import in.koreatech.payment.model.domain.PaymentConfirmInfo;
import in.koreatech.payment.model.domain.TakeoutPaymentInfo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final UserAuthenticationService userAuthenticationService;
    private final TemporaryPaymentService temporaryPaymentService;
    private final PaymentConfirmService paymentConfirmService;
    private final PaymentCancelService paymentCancelService;
    private final PaymentQueryService paymentQueryService;

    @Transactional
    public TemporaryPaymentResponse createTemporaryDeliveryPayment(
        String accessToken, TemporaryDeliveryPaymentSaveRequest request
    ) {
        User user = userAuthenticationService.authenticateUser(accessToken);
        DeliveryPaymentInfo deliveryPaymentInfo = DeliveryPaymentInfo.of(
            request.phoneNumber(),
            request.address(),
            request.toOwner(),
            request.toRider(),
            request.provideCutlery(),
            request.totalMenuPrice(),
            request.deliveryTip(),
            request.totalAmount()
        );
        return temporaryPaymentService.createDeliveryPayment(user, deliveryPaymentInfo);
    }

    @Transactional
    public TemporaryPaymentResponse createTemporaryTakeoutPayment(
        String accessToken, TemporaryTakeoutPaymentSaveRequest request
    ) {
        User user = userAuthenticationService.authenticateUser(accessToken);
        TakeoutPaymentInfo takeoutPaymentInfo = TakeoutPaymentInfo.of(
            request.phoneNumber(),
            request.toOwner(),
            request.provideCutlery(),
            request.totalMenuPrice(),
            request.totalAmount()
        );
        return temporaryPaymentService.createTakeoutPayment(user, takeoutPaymentInfo);
    }

    @Transactional
    public PaymentConfirmResponse confirmPayment(String accessToken, PaymentConfirmRequest request) {
        User user = userAuthenticationService.authenticateUser(accessToken);
        PaymentConfirmInfo paymentConfirmInfo = PaymentConfirmInfo.of(
            request.paymentKey(),
            request.orderId(),
            request.amount()
        );
        return paymentConfirmService.confirmPayment(user, paymentConfirmInfo);
    }

    @Transactional
    public PaymentCancelResponse cancelPayment(String accessToken, Integer paymentId, PaymentCancelRequest request) {
        User user = userAuthenticationService.authenticateUser(accessToken);
        PaymentCancelInfo paymentCancelInfo = PaymentCancelInfo.of(request.cancelReason());
        return paymentCancelService.cancelPayment(user, paymentId, paymentCancelInfo);
    }

    public PaymentResponse getPayment(String accessToken, Integer paymentId) {
        User user = userAuthenticationService.authenticateUser(accessToken);
        return paymentQueryService.getPayment(user, paymentId);
    }
}
