package in.koreatech.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.koreatech.koin.domain.order.cart.model.Cart;
import in.koreatech.koin.domain.order.cart.repository.CartRepository;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.payment.dto.request.TemporaryDeliveryPaymentSaveRequest;
import in.koreatech.payment.dto.request.TemporaryTakeoutPaymentSaveRequest;
import in.koreatech.payment.dto.response.TemporaryPaymentResponse;
import in.koreatech.payment.exception.OrderPriceMismatchException;
import in.koreatech.payment.gateway.pg.PaymentGatewayService;
import in.koreatech.payment.model.domain.TemporaryMenuItems;
import in.koreatech.payment.model.redis.TemporaryPayment;
import in.koreatech.payment.repository.redis.TemporaryPaymentRedisRepository;
import in.koreatech.payment.util.TemporaryMenuItemConverter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TemporaryPaymentService {

    private final CartRepository cartRepository;
    private final PaymentGatewayService paymentGatewayService;
    private final TemporaryPaymentRedisRepository temporaryPaymentRedisRepository;

    @Transactional
    public TemporaryPaymentResponse createDeliveryPayment(User user, TemporaryDeliveryPaymentSaveRequest request) {
        Cart cart = cartRepository.getCartByUserId(user.getId());
        OrderableShop orderableShop = cart.getOrderableShop();

        List<TemporaryMenuItems> temporaryMenuItems = TemporaryMenuItemConverter.fromCart(cart);
        int totalProductPrice = cart.calculateItemsAmount();
        int deliveryFee = orderableShop.calculateDeliveryFee(totalProductPrice);
        int finalAmount = totalProductPrice + deliveryFee;

        validateDeliveryPrice(request, totalProductPrice, deliveryFee, finalAmount);

        String pgOrderId = paymentGatewayService.generatePgOrderId();
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
    public TemporaryPaymentResponse createTakeoutPayment(User user, TemporaryTakeoutPaymentSaveRequest request) {
        Cart cart = cartRepository.getCartByUserId(user.getId());
        OrderableShop orderableShop = cart.getOrderableShop();

        List<TemporaryMenuItems> temporaryMenuItems = TemporaryMenuItemConverter.fromCart(cart);
        int totalProductPrice = cart.calculateItemsAmount();
        int finalAmount = totalProductPrice;

        validateTakeoutPrice(request, totalProductPrice, finalAmount);

        String pgOrderId = paymentGatewayService.generatePgOrderId();
        TemporaryPayment takeoutEntity = TemporaryPayment.toTakeOutEntity(
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

        temporaryPaymentRedisRepository.save(takeoutEntity);
        return TemporaryPaymentResponse.of(pgOrderId);
    }

    private void validateDeliveryPrice(
        TemporaryDeliveryPaymentSaveRequest request, int totalProductPrice, int deliveryFee, int finalAmount
    ) {
        if (!request.totalMenuPrice().equals(totalProductPrice)
            || !request.deliveryTip().equals(deliveryFee)
            || !request.totalAmount().equals(finalAmount)
        ) {
            throw OrderPriceMismatchException.withDetail(
                "totalProductPrice : " + totalProductPrice + "deliveryFee : " + deliveryFee + "totalAmount : "
                    + totalProductPrice + "finalAmount : " + finalAmount);
        }
    }

    private void validateTakeoutPrice(
        TemporaryTakeoutPaymentSaveRequest request, int totalProductPrice, int finalAmount
    ) {
        if (!request.totalMenuPrice().equals(totalProductPrice)
            || !request.totalAmount().equals(finalAmount)
        ) {
            throw OrderPriceMismatchException.withDetail(
                "totalProductPrice : " + totalProductPrice + "finalAmount : " + finalAmount);
        }
    }
}
