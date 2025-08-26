package in.koreatech.payment.gateway.toss;

import java.util.List;

import org.springframework.stereotype.Service;

import in.koreatech.payment.gateway.pg.PaymentGatewayService;
import in.koreatech.payment.gateway.pg.dto.PaymentCancelResponse;
import in.koreatech.payment.gateway.pg.dto.PaymentConfirmationResponse;

@Service
public class TossPaymentGatewayService implements PaymentGatewayService {

    public PaymentConfirmationResponse confirmPayment(String paymentKey, String pgOrderId, Integer amount) {
        return null;
    }

    public List<PaymentCancelResponse> cancelPayment(String paymentKey, String cancelReason) {
        return List.of();
    }
}
