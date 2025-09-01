package in.koreatech.payment.service;

import in.koreatech.payment.model.entity.Order;
import in.koreatech.payment.model.entity.OrderMenu;
import in.koreatech.payment.model.entity.Payment;
import in.koreatech.payment.repository.mysql.OrderMenuRepository;
import in.koreatech.payment.repository.mysql.PaymentRepository;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.payment.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "koinPaymentTransactionManager", readOnly = true)
public class PaymentQueryService {

    private final PaymentRepository paymentRepository;
    private final OrderMenuRepository orderMenuRepository;

    public PaymentResponse getPayment(User user, Integer paymentId) {
        Payment payment = paymentRepository.getById(paymentId);
        payment.validateUserIdMatches(user.getId());

        Order order = payment.getOrder();
        List<OrderMenu> orderMenus = orderMenuRepository.findAllByOrderId(order.getId());

        return PaymentResponse.of(payment, order, orderMenus);
    }
}
