package in.koreatech.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.koin.domain.order.cart.model.Cart;
import in.koreatech.koin.domain.order.cart.repository.CartRepository;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.payment.dto.response.TemporaryPaymentResponse;
import in.koreatech.payment.gateway.pg.PaymentGatewayService;
import in.koreatech.payment.mapper.TemporaryMenuItemsMapper;
import in.koreatech.payment.model.domain.DeliveryPaymentInfo;
import in.koreatech.payment.model.domain.TakeoutPaymentInfo;
import in.koreatech.payment.model.domain.TemporaryMenuItems;
import in.koreatech.payment.model.redis.TemporaryPayment;
import in.koreatech.payment.repository.redis.TemporaryPaymentRedisRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemporaryPaymentService {

    private final CartRepository cartRepository;
    private final PaymentGatewayService paymentGatewayService;
    private final TemporaryPaymentRedisRepository temporaryPaymentRedisRepository;
    private final TemporaryMenuItemsMapper temporaryMenuItemsMapper;

    public TemporaryPaymentResponse createDeliveryPayment(User user, DeliveryPaymentInfo deliveryPaymentInfo) {
        Cart cart = cartRepository.getCartByUserId(user.getId());
        OrderableShop orderableShop = cart.getOrderableShop();

        List<TemporaryMenuItems> temporaryMenuItems = temporaryMenuItemsMapper.fromCart(cart);
        int totalProductPrice = cart.calculateItemsAmount();
        int deliveryFee = orderableShop.calculateDeliveryFee(totalProductPrice);
        int finalAmount = totalProductPrice + deliveryFee;

        deliveryPaymentInfo.validatePrice(totalProductPrice, deliveryFee, finalAmount);

        String pgOrderId = paymentGatewayService.generatePgOrderId();
        TemporaryPayment deliveryEntity = TemporaryPayment.toDeliveryEntity(
            pgOrderId,
            user.getId(),
            orderableShop.getId(),
            deliveryPaymentInfo.phoneNumber(),
            deliveryPaymentInfo.address(),
            deliveryPaymentInfo.toOwner(),
            deliveryPaymentInfo.toRider(),
            deliveryPaymentInfo.provideCutlery(),
            totalProductPrice,
            deliveryFee,
            finalAmount,
            temporaryMenuItems
        );

        temporaryPaymentRedisRepository.save(deliveryEntity);
        return TemporaryPaymentResponse.of(pgOrderId);
    }

    public TemporaryPaymentResponse createTakeoutPayment(User user, TakeoutPaymentInfo takeoutPaymentInfo) {
        Cart cart = cartRepository.getCartByUserId(user.getId());
        OrderableShop orderableShop = cart.getOrderableShop();

        List<TemporaryMenuItems> temporaryMenuItems = temporaryMenuItemsMapper.fromCart(cart);
        int totalProductPrice = cart.calculateItemsAmount();
        int finalAmount = totalProductPrice;

        takeoutPaymentInfo.validatePrice(totalProductPrice, finalAmount);

        String pgOrderId = paymentGatewayService.generatePgOrderId();
        TemporaryPayment takeoutEntity = TemporaryPayment.toTakeOutEntity(
            pgOrderId,
            user.getId(),
            orderableShop.getId(),
            takeoutPaymentInfo.phoneNumber(),
            takeoutPaymentInfo.toOwner(),
            takeoutPaymentInfo.provideCutlery(),
            totalProductPrice,
            finalAmount,
            temporaryMenuItems
        );

        temporaryPaymentRedisRepository.save(takeoutEntity);
        return TemporaryPaymentResponse.of(pgOrderId);
    }
}
