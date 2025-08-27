package in.koreatech.payment.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.koin.domain.order.cart.model.Cart;
import in.koreatech.koin.domain.order.cart.repository.CartRepository;
import in.koreatech.koin.domain.order.model.Order;
import in.koreatech.koin.domain.order.model.OrderMenu;
import in.koreatech.koin.domain.order.model.Payment;
import in.koreatech.koin.domain.order.model.PaymentCancel;
import in.koreatech.koin.domain.order.model.PaymentStatus;
import in.koreatech.koin.domain.order.repository.OrderMenuRepository;
import in.koreatech.koin.domain.order.repository.OrderRepository;
import in.koreatech.koin.domain.order.repository.PaymentCancelRepository;
import in.koreatech.koin.domain.order.repository.PaymentRepository;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.order.shop.repository.OrderableShopRepository;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.koin.domain.user.repository.UserRepository;
import in.koreatech.payment.common.auth.JwtProvider;
import in.koreatech.payment.dto.request.TemporaryDeliveryPaymentSaveRequest;
import in.koreatech.payment.dto.request.TemporaryTakeoutPaymentSaveRequest;
import in.koreatech.payment.dto.response.PaymentResponse;
import in.koreatech.payment.exception.OrderPriceMismatchException;
import in.koreatech.payment.exception.PaymentAlreadyCanceledException;
import in.koreatech.payment.exception.PaymentCancelException;
import in.koreatech.payment.gateway.pg.PgOrderIdGenerator;
import in.koreatech.payment.gateway.pg.dto.PaymentCancelResponse;
import in.koreatech.payment.gateway.pg.dto.PaymentConfirmResponse;
import in.koreatech.payment.gateway.toss.TossPaymentClient;
import in.koreatech.payment.gateway.toss.TossPaymentGatewayService;
import in.koreatech.payment.gateway.toss.dto.response.TossPaymentCancelResponse;
import in.koreatech.payment.mapper.PaymentCancelMapper;
import in.koreatech.payment.mapper.PaymentMapper;
import in.koreatech.payment.model.domain.TemporaryMenuItems;
import in.koreatech.payment.model.redis.TemporaryPayment;
import in.koreatech.payment.repository.redis.TemporaryPaymentRedisRepository;
import in.koreatech.payment.util.TemporaryMenuItemConverter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TossService implements PaymentService {

    private final PgOrderIdGenerator pgOrderIdGenerator;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentIdempotencyKeyService paymentIdempotencyKeyService;
    private final PaymentCancelRepository paymentCancelRepository;
    private final CartRepository cartRepository;
    private final TemporaryPaymentRedisRepository temporaryPaymentRedisRepository;
    private final OrderableShopRepository orderableShopRepository;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final TossPaymentGatewayService tossPaymentGatewayService;
    private final PaymentMapper paymentMapper;
    private final PaymentCancelMapper paymentCancelMappers;

    @Transactional
    public String createTemporaryDeliveryPayment(String accessToken, TemporaryDeliveryPaymentSaveRequest request) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);

        Cart cart = cartRepository.getCartByUserId(user.getId());

        OrderableShop orderableShop = cart.getOrderableShop();
        List<TemporaryMenuItems> temporaryMenuItems = TemporaryMenuItemConverter.fromCart(cart);
        int totalProductPrice = cart.calculateItemsAmount();
        int deliveryFee = orderableShop.calculateDeliveryFee(totalProductPrice);
        int finalAmount = totalProductPrice + deliveryFee;

        if (!request.totalMenuPrice().equals(totalProductPrice)
            || !request.deliveryTip().equals(deliveryFee)
            || !request.totalAmount().equals(finalAmount)
        ) {
            throw OrderPriceMismatchException.withDetail(
                "totalProductPrice : " + totalProductPrice + "deliveryFee : " + deliveryFee + "totalAmount : "
                    + totalProductPrice + "finalAmount : " + finalAmount);
        }

        String pgOrderId = pgOrderIdGenerator.generatePgOrderId();

        TemporaryPayment deliveryEntity = TemporaryPayment.toDeliveryEntity(
            pgOrderId,
            user.getId(),
            orderableShop.getId(),
            request.phoneNumber(),
            request.address(),
            request.toOwner(),
            request.toRider(),
            request.provideCutlery(),
            totalProductPrice,
            deliveryFee,
            finalAmount,
            temporaryMenuItems
        );

        temporaryPaymentRedisRepository.save(deliveryEntity);
        return pgOrderId;
    }

    @Transactional
    public String createTemporaryTakeoutPayment(String accessToken, TemporaryTakeoutPaymentSaveRequest request) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);

        Cart cart = cartRepository.getCartByUserId(user.getId());

        OrderableShop orderableShop = cart.getOrderableShop();
        List<TemporaryMenuItems> temporaryMenuItems = TemporaryMenuItemConverter.fromCart(cart);
        int totalProductPrice = cart.calculateItemsAmount();
        int finalAmount = totalProductPrice;

        if (!request.totalMenuPrice().equals(totalProductPrice)
            || !request.totalAmount().equals(finalAmount)
        ) {
            throw OrderPriceMismatchException.withDetail(
                "totalProductPrice : " + totalProductPrice + "finalAmount : " + finalAmount);
        }

        String pgOrderId = pgOrderIdGenerator.generatePgOrderId();

        TemporaryPayment deliveryEntity = TemporaryPayment.toTakeOutEntity(
            pgOrderId,
            user.getId(),
            orderableShop.getId(),
            request.phoneNumber(),
            request.toOwner(),
            request.provideCutlery(),
            totalProductPrice,
            finalAmount,
            temporaryMenuItems
        );

        temporaryPaymentRedisRepository.save(deliveryEntity);
        return pgOrderId;
    }

    @Transactional
    public in.koreatech.payment.dto.response.PaymentConfirmResponse confirmPayment(String accessToken, String paymentKey, String orderId,
        Integer amount) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);
        TemporaryPayment temporaryPayment = temporaryPaymentRedisRepository.getById(orderId);
        temporaryPayment.validateMatches(orderId, user.getId(), amount);

        PaymentConfirmResponse paymentConfirmResponse = tossPaymentGatewayService.confirmPayment(paymentKey,
            orderId, amount);

        // TODO. 롤백 로직 수정
        // applicationEventPublisher.publishEvent(
        //     TossPaymentRollBackEvent.from(paymentKey, temporaryPayment, paymentConfirmResponse));

        OrderableShop orderableShop = orderableShopRepository.getById(temporaryPayment.getOrderableShopId());
        Order order = temporaryPayment.toOrder(user, orderableShop);
        orderRepository.save(order);

        List<OrderMenu> orderMenus = temporaryPayment.getTemporaryMenuItems().stream()
            .map(temporaryMenuItems -> temporaryMenuItems.toOrderMenu(order))
            .toList();
        orderMenuRepository.saveAll(orderMenus);

        Payment payment = paymentMapper.toEntity(order, paymentConfirmResponse);
        paymentRepository.save(payment);
        temporaryPaymentRedisRepository.deleteById(orderId);
        cartRepository.deleteByUserId(user.getId());
        return in.koreatech.payment.dto.response.PaymentConfirmResponse.of(payment, order, orderMenus);
    }

    @Transactional
    public List<PaymentCancel> cancelPayment(String accessToken, Integer paymentId, String cancelReason) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);
        Payment payment = paymentRepository.getById(paymentId);
        if (payment.getPaymentStatus().isCanceled()) {
            throw PaymentAlreadyCanceledException.withDetail("paymentId : " + payment.getId());
        }
        payment.validateUserIdMatches(user.getId());

        String paymentIdempotencyKey = paymentIdempotencyKeyService.getOrCreate(user.getId());
        PaymentCancelResponse response = tossPaymentGatewayService.cancelPayment(payment.getPaymentKey(), cancelReason, paymentIdempotencyKey);
        payment.cancel();

        List<PaymentCancel> paymentCancels = paymentCancelMappers.toEntity(payment, response);
        paymentCancelRepository.saveAll(paymentCancels);
        return paymentCancels;
    }

    public PaymentResponse getPayment(String accessToken, Integer paymentId) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);
        Payment payment = paymentRepository.getById(paymentId);
        payment.validateUserIdMatches(user.getId());

        Order order = payment.getOrder();
        List<OrderMenu> orderMenus = orderMenuRepository.findAllByOrderId(order.getId());

        return PaymentResponse.of(payment, order, orderMenus);
    }
}
