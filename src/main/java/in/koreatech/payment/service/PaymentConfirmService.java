package in.koreatech.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.koin.domain.order.cart.repository.CartRepository;
import in.koreatech.koin.domain.order.model.Order;
import in.koreatech.koin.domain.order.model.OrderMenu;
import in.koreatech.koin.domain.order.model.Payment;
import in.koreatech.koin.domain.order.model.PaymentStatus;
import in.koreatech.koin.domain.order.repository.OrderMenuRepository;
import in.koreatech.koin.domain.order.repository.OrderRepository;
import in.koreatech.koin.domain.order.repository.PaymentRepository;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.order.shop.repository.OrderableShopRepository;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.payment.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.exception.PaymentConfirmException;
import in.koreatech.payment.gateway.pg.PaymentGatewayService;
import in.koreatech.payment.gateway.pg.dto.PgPaymentConfirmResponse;
import in.koreatech.payment.mapper.PaymentMapper;
import in.koreatech.payment.model.domain.PaymentConfirmInfo;
import in.koreatech.payment.model.redis.TemporaryPayment;
import in.koreatech.payment.repository.redis.TemporaryPaymentRedisRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentConfirmService {

    private final PaymentGatewayService paymentGatewayService;
    private final OrderableShopRepository orderableShopRepository;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final PaymentRepository paymentRepository;
    private final TemporaryPaymentRedisRepository temporaryPaymentRedisRepository;
    private final CartRepository cartRepository;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentConfirmResponse confirmPayment(User user, PaymentConfirmInfo paymentConfirmInfo) {
        TemporaryPayment temporaryPayment = temporaryPaymentRedisRepository.getById(paymentConfirmInfo.orderId());
        temporaryPayment.validateMatches(paymentConfirmInfo.orderId(), user.getId(), paymentConfirmInfo.amount());

        PgPaymentConfirmResponse pgResponse = paymentGatewayService.confirmPayment(paymentConfirmInfo.paymentKey(),
            paymentConfirmInfo.orderId(), paymentConfirmInfo.amount());
        validatePaymentStatus(pgResponse.status());

        OrderableShop orderableShop = orderableShopRepository.getById(temporaryPayment.getOrderableShopId());
        Order order = temporaryPayment.toOrder(user, orderableShop);
        orderRepository.save(order);

        List<OrderMenu> orderMenus = createOrderMenus(temporaryPayment, order);
        orderMenuRepository.saveAll(orderMenus);

        Payment payment = paymentMapper.toEntity(order, pgResponse);
        paymentRepository.save(payment);

        cleanupAfterPaymentConfirm(paymentConfirmInfo.orderId(), user.getId());

        return PaymentConfirmResponse.of(payment, order, orderMenus);
    }

    private void validatePaymentStatus(String status) {
        PaymentStatus paymentStatus = PaymentStatus.valueOf(status);
        if (!paymentStatus.isDone()) {
            throw PaymentConfirmException.withDetail("paymentStatus : " + status);
        }
    }

    private List<OrderMenu> createOrderMenus(TemporaryPayment temporaryPayment, Order order) {
        return temporaryPayment.getTemporaryMenuItems().stream()
            .map(temporaryMenuItems -> temporaryMenuItems.toOrderMenu(order))
            .toList();
    }

    private void cleanupAfterPaymentConfirm(String orderId, Integer userId) {
        temporaryPaymentRedisRepository.deleteById(orderId);
        cartRepository.deleteByUserId(userId);
    }
}
