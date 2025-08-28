package in.koreatech.payment.service;

import in.koreatech.koin.domain.order.model.Order;
import in.koreatech.koin.domain.order.model.OrderMenu;
import in.koreatech.koin.domain.order.model.Payment;
import in.koreatech.koin.domain.order.repository.OrderMenuRepository;
import in.koreatech.koin.domain.order.repository.PaymentRepository;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.payment.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
