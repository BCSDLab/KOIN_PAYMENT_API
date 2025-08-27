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
import in.koreatech.payment.dto.response.PaymentCancelResponse;
import in.koreatech.payment.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.dto.response.PaymentResponse;
import in.koreatech.payment.dto.response.TemporaryPaymentResponse;
import in.koreatech.payment.exception.OrderPriceMismatchException;
import in.koreatech.payment.exception.PaymentAlreadyCanceledException;
import in.koreatech.payment.exception.PaymentCancelException;
import in.koreatech.payment.exception.PaymentConfirmException;
import in.koreatech.payment.gateway.pg.PaymentGatewayService;
import in.koreatech.payment.gateway.pg.PgOrderIdGenerator;
import in.koreatech.payment.gateway.pg.dto.PgPaymentCancelResponse;
import in.koreatech.payment.gateway.pg.dto.PgPaymentConfirmResponse;
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
public class PaymentService {

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
    private final PaymentGatewayService paymentGatewayService;
    private final PaymentMapper paymentMapper;
    private final PaymentCancelMapper paymentCancelMappers;

    @Transactional
    public TemporaryPaymentResponse createTemporaryDeliveryPayment(String accessToken, TemporaryDeliveryPaymentSaveRequest request) {
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
        return TemporaryPaymentResponse.of(pgOrderId);
    }

    @Transactional
    public TemporaryPaymentResponse createTemporaryTakeoutPayment(String accessToken, TemporaryTakeoutPaymentSaveRequest request) {
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
        return TemporaryPaymentResponse.of(pgOrderId);
    }

    @Transactional
    public PaymentConfirmResponse confirmPayment(String accessToken, String paymentKey, String orderId, Integer amount) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);
        TemporaryPayment temporaryPayment = temporaryPaymentRedisRepository.getById(orderId);
        temporaryPayment.validateMatches(orderId, user.getId(), amount);

        PgPaymentConfirmResponse pgPaymentConfirmResponse = paymentGatewayService.confirmPayment(paymentKey, orderId, amount);
        PaymentStatus paymentStatus = PaymentStatus.valueOf(pgPaymentConfirmResponse.status());
        if (!paymentStatus.isDone()) {
            throw PaymentConfirmException.withDetail("paymentStatus : " + pgPaymentConfirmResponse.status());
        }

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

        Payment payment = paymentMapper.toEntity(order, pgPaymentConfirmResponse);
        paymentRepository.save(payment);
        temporaryPaymentRedisRepository.deleteById(orderId);
        cartRepository.deleteByUserId(user.getId());
        return PaymentConfirmResponse.of(payment, order, orderMenus);
    }

    @Transactional
    public PaymentCancelResponse cancelPayment(String accessToken, Integer paymentId, String cancelReason) {
        Integer userId = jwtProvider.getUserId(accessToken);
        User user = userRepository.getById(userId);
        Payment payment = paymentRepository.getById(paymentId);
        if (payment.getPaymentStatus().isCanceled()) {
            throw PaymentAlreadyCanceledException.withDetail("paymentId : " + payment.getId());
        }
        payment.validateUserIdMatches(user.getId());

        String paymentIdempotencyKey = paymentIdempotencyKeyService.getOrCreate(user.getId());
        PgPaymentCancelResponse pgPaymentCancelResponse = paymentGatewayService.cancelPayment(payment.getPaymentKey(), cancelReason, paymentIdempotencyKey);
        if (!PaymentStatus.valueOf(pgPaymentCancelResponse.status()).isCanceled()) {
            throw PaymentCancelException.withDetail("paymentStatus : " + pgPaymentCancelResponse.status());
        }

        payment.cancel();

        List<PaymentCancel> paymentCancels = paymentCancelMappers.toEntity(payment, pgPaymentCancelResponse);
        paymentCancelRepository.saveAll(paymentCancels);
        return PaymentCancelResponse.from(paymentCancels);
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
