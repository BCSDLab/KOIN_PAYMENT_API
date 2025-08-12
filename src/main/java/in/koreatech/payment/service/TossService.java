package in.koreatech.payment.service;

import java.util.List;
import java.util.UUID;

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
import in.koreatech.payment.client.TossPaymentClient;
import in.koreatech.payment.client.dto.response.PaymentCancelResponse;
import in.koreatech.payment.client.dto.response.TossPaymentConfirmResponse;
import in.koreatech.payment.common.auth.JwtTokenResolver;
import in.koreatech.payment.dto.request.TemporaryDeliveryPaymentSaveRequest;
import in.koreatech.payment.dto.request.TemporaryTakeoutPaymentSaveRequest;
import in.koreatech.payment.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.dto.response.PaymentResponse;
import in.koreatech.payment.event.TossPaymentRollBackEvent;
import in.koreatech.payment.exception.OrderPriceMismatchException;
import in.koreatech.payment.exception.PaymentAlreadyCanceledException;
import in.koreatech.payment.exception.PaymentCancelException;
import in.koreatech.payment.exception.PaymentConfirmException;
import in.koreatech.payment.model.domain.TemporaryMenuItems;
import in.koreatech.koin.domain.order.model.PaymentIdempotencyKey;
import in.koreatech.payment.model.redis.TemporaryPayment;
import in.koreatech.koin.domain.order.repository.PaymentIdempotencyKeyRepository;
import in.koreatech.payment.repository.redis.TemporaryPaymentRedisRepository;
import in.koreatech.payment.util.OrderIdGenerator;
import in.koreatech.payment.util.TemporaryMenuItemConverter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TossService implements PaymentService {

    private final OrderIdGenerator orderIdGenerator;
    private final JwtTokenResolver jwtTokenResolver;
    private final UserRepository userRepository;
    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;
    private final PaymentIdempotencyKeyRepository paymentIdempotencyKeyRepository;
    private final PaymentCancelRepository paymentCancelRepository;
    private final CartRepository cartRepository;
    private final TemporaryPaymentRedisRepository temporaryPaymentRedisRepository;
    private final OrderableShopRepository orderableShopRepository;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public String createTemporaryDeliveryPayment(String accessToken, TemporaryDeliveryPaymentSaveRequest request) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
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
            throw OrderPriceMismatchException.withDetail("totalProductPrice : " + totalProductPrice + "deliveryFee : " + deliveryFee + "totalAmount : " + totalProductPrice + "finalAmount : " + finalAmount);
        }

        String orderId = orderIdGenerator.generateOrderId();

        TemporaryPayment deliveryEntity = TemporaryPayment.toDeliveryEntity(
            orderId,
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
        return orderId;
    }

    @Transactional
    public String createTemporaryTakeoutPayment(String accessToken, TemporaryTakeoutPaymentSaveRequest request) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        User user = userRepository.getById(userId);

        Cart cart = cartRepository.getCartByUserId(user.getId());

        OrderableShop orderableShop = cart.getOrderableShop();
        List<TemporaryMenuItems> temporaryMenuItems = TemporaryMenuItemConverter.fromCart(cart);
        int totalProductPrice = cart.calculateItemsAmount();
        int finalAmount = totalProductPrice;

        if (!request.totalMenuPrice().equals(totalProductPrice)
            || !request.totalAmount().equals(finalAmount)
        ) {
            throw OrderPriceMismatchException.withDetail("totalProductPrice : " + totalProductPrice + "finalAmount : " + finalAmount);
        }

        String orderId = orderIdGenerator.generateOrderId();

        TemporaryPayment deliveryEntity = TemporaryPayment.toTakeOutEntity(
            orderId,
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
        return orderId;
    }

    @Transactional
    public PaymentConfirmResponse confirmPayment(String accessToken, String paymentKey, String orderId, Integer amount) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        User user = userRepository.getById(userId);
        TemporaryPayment temporaryPayment = temporaryPaymentRedisRepository.getById(orderId);
        temporaryPayment.validateMatches(orderId, user.getId(), amount);

        TossPaymentConfirmResponse tossPaymentResponse = tossPaymentClient.requestConfirm(paymentKey, orderId, amount);
        PaymentStatus paymentStatus = PaymentStatus.valueOf(tossPaymentResponse.status());
        if (!paymentStatus.isDone()) {
            throw PaymentConfirmException.withDetail("paymentStatus : " + tossPaymentResponse.status());
        }

        applicationEventPublisher.publishEvent(
            TossPaymentRollBackEvent.from(paymentKey, temporaryPayment, tossPaymentResponse));

        OrderableShop orderableShop = orderableShopRepository.getById(temporaryPayment.getOrderableShopId());
        Order order = temporaryPayment.toOrder(user, orderableShop);
        orderRepository.save(order);

        List<OrderMenu> orderMenus = temporaryPayment.getTemporaryMenuItems().stream()
            .map(temporaryMenuItems -> temporaryMenuItems.toOrderMenu(order))
            .toList();
        orderMenuRepository.saveAll(orderMenus);

        Payment payment = tossPaymentResponse.toEntity(order);
        paymentRepository.save(payment);
        temporaryPaymentRedisRepository.deleteById(orderId);
        cartRepository.deleteByUserId(user.getId());
        return PaymentConfirmResponse.of(payment, order, orderMenus);
    }

    @Transactional
    public List<PaymentCancel> cancelPayment(String accessToken, String paymentKey, String cancelReason) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        User user = userRepository.getById(userId);
        Payment payment = paymentRepository.getByPaymentKey(paymentKey);
        if (payment.getPaymentStatus().isCanceled()) {
            throw PaymentAlreadyCanceledException.withDetail("paymentId : " + payment.getId());
        }
        payment.validateUserIdMatches(user.getId());

        PaymentIdempotencyKey paymentIdempotencyKey = paymentIdempotencyKeyRepository
            .findByUserId(user.getId())
            .map(idempotencyKey -> {
                if (idempotencyKey.isOlderThanExpireDays()) {
                    idempotencyKey.updateIdempotencyKey(UUID.randomUUID().toString());
                }
                return idempotencyKey;
            })
            .orElseGet(() -> paymentIdempotencyKeyRepository.save(
                PaymentIdempotencyKey.builder()
                    .userId(user.getId())
                    .idempotencyKey(UUID.randomUUID().toString())
                    .build()
            ));

        PaymentCancelResponse response = tossPaymentClient.requestCancel(paymentKey, cancelReason,
            paymentIdempotencyKey.getIdempotencyKey());
        if (!PaymentStatus.valueOf(response.status()).isCanceled()) {
            throw PaymentCancelException.withDetail("paymentStatus : " + response.status());
        }

        payment.cancel();
        List<PaymentCancel> paymentCancels = response.getPaymentCancels(payment);
        paymentCancelRepository.saveAll(paymentCancels);
        return paymentCancels;
    }

    public PaymentResponse getPayment(String accessToken, Integer paymentId) {
        Integer userId = jwtTokenResolver.getUserId(accessToken);
        User user = userRepository.getById(userId);
        Payment payment = paymentRepository.getById(paymentId);
        payment.validateUserIdMatches(user.getId());

        Order order = payment.getOrder();
        List<OrderMenu> orderMenus = orderMenuRepository.findAllByOrderId(order.getId());

        return PaymentResponse.of(payment, order, orderMenus);
    }
}
