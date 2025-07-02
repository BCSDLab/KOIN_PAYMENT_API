package in.koreatech.payment.dto.response;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import in.koreatech.koin.domain.order.model.Order;
import in.koreatech.koin.domain.order.model.OrderDelivery;
import in.koreatech.koin.domain.order.model.OrderTakeout;
import in.koreatech.koin.domain.order.model.Payment;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.shop.model.shop.Shop;
import in.koreatech.payment.model.domain.TemporaryMenuItems;
import in.koreatech.payment.model.domain.TemporaryMenuOption;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonNaming(value = SnakeCaseStrategy.class)
public record PaymentConfirmResponse(
    @Schema(description = "결제 고유 id", example = "1", requiredMode = REQUIRED)
    Integer id,

    @Schema(description = "배달 주소", example = "충청남도 천안시 동남구 병천면 충절로 1600 은솔관 422호", requiredMode = NOT_REQUIRED)
    String deliveryAddress,

    @Schema(description = "가게 주소", example = "충청남도 천안시 동남구 병천면 충절로 1600 은솔관 422호", requiredMode = NOT_REQUIRED)
    String shopAddress,

    @Schema(description = "사장님에게", example = "리뷰 이벤트 감사합니다.", requiredMode = REQUIRED)
    String toOwner,

    @Schema(description = "라이더에게", example = "문 앞에 놔주세요.", requiredMode = NOT_REQUIRED)
    String toRider,

    @Schema(description = "결제 금액", example = "1000", requiredMode = REQUIRED)
    Integer amount,

    @Schema(description = "상점 이름", example = "굿모닝 살로만 치킨", requiredMode = REQUIRED)
    String shopName,

    @Schema(description = "주문 메뉴 목록", requiredMode = REQUIRED)
    List<InnerCartItemResponse> items,

    @Schema(description = "주문 방법", example = "DELIVERY", requiredMode = REQUIRED)
    String orderType,

    @Schema(description = "결제 요청 일시", example = "2025.06.21 21:00", requiredMode = REQUIRED)
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    LocalDateTime requestedAt,

    @Schema(description = "결제 승인 일시", example = "2025.06.21 21:00", requiredMode = REQUIRED)
    @JsonFormat(pattern = "yyyy.MM.dd HH:mm")
    LocalDateTime approvedAt,

    @Schema(description = "결제 수단", example = "카드", requiredMode = REQUIRED)
    String paymentMethod
) {

    @JsonNaming(value = SnakeCaseStrategy.class)
    public record InnerCartItemResponse(
        @Schema(description = "메뉴 이름", example = "허니콤보", requiredMode = REQUIRED)
        String name,

        @Schema(description = "수량", example = "1", requiredMode = REQUIRED)
        Integer quantity,

        @Schema(description = "선택한 옵션 목록", requiredMode = NOT_REQUIRED)
        List<InnerMenuOptionResponse> options
    ) {
        public static InnerCartItemResponse from(TemporaryMenuItems temporaryMenuItems) {
            List<InnerMenuOptionResponse> optionResponses = temporaryMenuItems.options().stream()
                .map(InnerMenuOptionResponse::from)
                .toList();

            return new InnerCartItemResponse(
                temporaryMenuItems.name(),
                temporaryMenuItems.quantity(),
                optionResponses
            );
        }
    }

    @JsonNaming(value = SnakeCaseStrategy.class)
    public record InnerMenuOptionResponse(
        @Schema(description = "옵션 그룹 이름", example = "소스 추가", requiredMode = NOT_REQUIRED)
        String optionGroupName,
        @Schema(description = "옵션 이름", example = "레드디핑 소스", requiredMode = REQUIRED)
        String optionName
    ) {
        public static InnerMenuOptionResponse from(TemporaryMenuOption temporaryMenuOption) {
            return new InnerMenuOptionResponse(
                temporaryMenuOption.optionGroupName(),
                temporaryMenuOption.optionName()
            );
        }
    }

    public static PaymentConfirmResponse takeOut(Payment payment, Order order,
        List<TemporaryMenuItems> temporaryMenuItems) {
        OrderableShop orderableShop = order.getOrderableShop();
        Shop shop = orderableShop.getShop();
        OrderTakeout orderTakeout = order.getOrderTakeout();

        return new PaymentConfirmResponse(
            payment.getId(),
            null,
            shop.getAddress(),
            orderTakeout.getToOwner(),
            null,
            payment.getAmount(),
            shop.getName(),
            temporaryMenuItems.stream()
                .map(InnerCartItemResponse::from)
                .toList(),
            order.getOrderType().name(),
            payment.getRequestedAt(),
            payment.getApprovedAt(),
            payment.getPaymentMethod().getDisplayName()
        );
    }

    public static PaymentConfirmResponse delivery(Payment payment, Order order,
        List<TemporaryMenuItems> temporaryMenuItems) {
        OrderableShop orderableShop = order.getOrderableShop();
        Shop shop = orderableShop.getShop();
        OrderDelivery orderDelivery = order.getOrderDelivery();

        return new PaymentConfirmResponse(
            payment.getId(),
            orderDelivery.getAddress(),
            shop.getAddress(),
            orderDelivery.getToOwner(),
            orderDelivery.getToRider(),
            payment.getAmount(),
            shop.getName(),
            temporaryMenuItems.stream()
                .map(InnerCartItemResponse::from)
                .toList(),
            order.getOrderType().name(),
            payment.getRequestedAt(),
            payment.getApprovedAt(),
            payment.getPaymentMethod().getDisplayName()
        );
    }
}
