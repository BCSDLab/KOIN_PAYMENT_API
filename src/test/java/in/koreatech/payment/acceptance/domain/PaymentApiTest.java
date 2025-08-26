package in.koreatech.payment.acceptance.domain;

import static in.koreatech.payment.client.dto.response.PaymentCancelResponse.CancelInfo;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import in.koreatech.koin.domain.order.cart.model.Cart;
import in.koreatech.koin.domain.order.model.Order;
import in.koreatech.koin.domain.order.model.OrderDelivery;
import in.koreatech.koin.domain.order.model.OrderMenu;
import in.koreatech.koin.domain.order.model.OrderTakeout;
import in.koreatech.koin.domain.order.model.OrderType;
import in.koreatech.koin.domain.order.model.Payment;
import in.koreatech.koin.domain.order.model.PaymentCancel;
import in.koreatech.koin.domain.order.model.PaymentIdempotencyKey;
import in.koreatech.koin.domain.order.model.PaymentMethod;
import in.koreatech.koin.domain.order.model.PaymentStatus;
import in.koreatech.koin.domain.order.repository.OrderMenuRepository;
import in.koreatech.koin.domain.order.repository.PaymentCancelRepository;
import in.koreatech.koin.domain.order.repository.PaymentRepository;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenu;
import in.koreatech.koin.domain.order.shop.model.entity.menu.OrderableShopMenuPrice;
import in.koreatech.koin.domain.order.shop.model.entity.shop.OrderableShop;
import in.koreatech.koin.domain.shop.model.shop.Shop;
import in.koreatech.koin.domain.user.model.User;
import in.koreatech.payment.acceptance.AcceptanceTest;
import in.koreatech.payment.acceptance.fixture.CartFixture;
import in.koreatech.payment.acceptance.fixture.OrderableShopFixture;
import in.koreatech.payment.acceptance.fixture.OrderableShopMenuFixture;
import in.koreatech.payment.acceptance.fixture.OrderableShopMenuPriceFixture;
import in.koreatech.payment.acceptance.fixture.PaymentIdempotencyKeyFixture;
import in.koreatech.payment.acceptance.fixture.ShopFixture;
import in.koreatech.payment.acceptance.fixture.UserFixture;
import in.koreatech.payment.gateway.toss.TossPaymentClient;
import in.koreatech.payment.client.dto.response.PaymentCancelResponse;
import in.koreatech.payment.client.dto.response.TossPaymentConfirmResponse;
import in.koreatech.payment.common.auth.JwtProvider;
import in.koreatech.payment.model.redis.TemporaryPayment;
import in.koreatech.payment.repository.redis.TemporaryPaymentRedisRepository;
import in.koreatech.payment.service.PaymentRollBackService;
import in.koreatech.payment.gateway.pg.PgOrderIdGenerator;

public class PaymentApiTest extends AcceptanceTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private TemporaryPaymentRedisRepository temporaryPaymentRedisRepository;

    @Autowired
    private OrderMenuRepository orderMenuRepository;

    @Autowired
    private PaymentCancelRepository paymentCancelRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserFixture userFixture;

    @Autowired
    private CartFixture cartFixture;

    @Autowired
    private ShopFixture shopFixture;

    @Autowired
    private OrderableShopFixture orderableShopFixture;

    @Autowired
    private OrderableShopMenuFixture orderableShopMenuFixture;

    @Autowired
    private OrderableShopMenuPriceFixture orderableShopMenuPriceFixture;

    @Autowired
    private PaymentIdempotencyKeyFixture paymentIdempotencyKeyFixture;

    @MockBean
    private TossPaymentClient tossPaymentClient;

    @MockBean
    private PaymentRollBackService paymentRollBackService;

    @MockBean
    private PgOrderIdGenerator pgOrderIdGenerator;

    private User user;
    private String token;
    private PaymentIdempotencyKey paymentIdempotencyKey;
    private Shop shop;
    private OrderableShop orderableShop;
    private Cart cart;
    private OrderableShopMenu menuGimbap;
    private OrderableShopMenuPrice gimbapPrice;

    @BeforeAll
    void setUp() {
        user = userFixture.코인_유저();
        // TODO. 프로덕션 리펙토링 이후 생성 객체로 부터 주입받도록 수정
        paymentIdempotencyKey = paymentIdempotencyKeyFixture.결제_멱등_키(user, UUID.randomUUID().toString());
        token = jwtProvider.createToken(user);
        shop = shopFixture.김밥천국();
        orderableShop = orderableShopFixture.주문_가능_김밥천국(shop);
        cart = cartFixture.장바구니(user, orderableShop);
        menuGimbap = orderableShopMenuFixture.주문_가능_상점_메뉴(orderableShop, "김밥");
        gimbapPrice = orderableShopMenuPriceFixture.주문_가능_상점_메뉴_가격(menuGimbap, "소고기 김밥", 6000);
        cartFixture.addOrderMenuItem(cart, menuGimbap, gimbapPrice, List.of(), 4);
    }

    @Nested
    @DisplayName("임시 결제 정보 저장 API - 성공")
    class TemporaryPaymentSuccess {

        @Test
        void 임시_배달_결제_정보_저장에_성공한다() throws Exception {
            given(pgOrderIdGenerator.generatePgOrderId()).willReturn("FAKE_ORDER_123");

            mockMvc.perform(
                    post("/payments/delivery/temporary")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "address": "충청남도 천안시 동남구 병천면 충절로 1600 은솔관 422호",
                              "phone_number": "01012345678",
                              "to_owner": "리뷰 이벤트 감사합니다.",
                              "to_rider": "문 앞에 놔주세요.",
                              "total_menu_price": 24000,
                              "delivery_tip": 0,
                              "provide_cutlery": true,
                              "total_amount": 24000
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                      {
                        "order_id": "FAKE_ORDER_123"
                      }
                    """)
                );

            TemporaryPayment temporaryPayment = temporaryPaymentRedisRepository.getById("FAKE_ORDER_123");
            assertSoftly(
                softly -> {
                    softly.assertThat(temporaryPayment.getPgOrderId()).isEqualTo("FAKE_ORDER_123");
                    softly.assertThat(temporaryPayment.getUserId()).isEqualTo(user.getId());
                    softly.assertThat(temporaryPayment.getOrderableShopId()).isEqualTo(orderableShop.getId());
                    softly.assertThat(temporaryPayment.getPhoneNumber()).isEqualTo("01012345678");
                    softly.assertThat(temporaryPayment.getOrderType()).isEqualTo(OrderType.DELIVERY);
                    softly.assertThat(temporaryPayment.getAddress()).isEqualTo("충청남도 천안시 동남구 병천면 충절로 1600 은솔관 422호");
                    softly.assertThat(temporaryPayment.getToOwner()).isEqualTo("리뷰 이벤트 감사합니다.");
                    softly.assertThat(temporaryPayment.getToRider()).isEqualTo("문 앞에 놔주세요.");
                    softly.assertThat(temporaryPayment.getProvideCutlery()).isEqualTo(true);
                    softly.assertThat(temporaryPayment.getTotalProductPrice()).isEqualTo(24000);
                    softly.assertThat(temporaryPayment.getDeliveryFee()).isEqualTo(0);
                    softly.assertThat(temporaryPayment.getTotalPrice()).isEqualTo(24000);
                    softly.assertThat(temporaryPayment.getTemporaryMenuItems().get(0).name()).isEqualTo("김밥");
                    softly.assertThat(temporaryPayment.getTemporaryMenuItems().get(0).quantity()).isEqualTo(4);
                    softly.assertThat(temporaryPayment.getTemporaryMenuItems().get(0).options()).isNull();
                }
            );
        }

        @Test
        void 임시_포장_결제_정보_저장에_성공한다() throws Exception {
            given(pgOrderIdGenerator.generatePgOrderId()).willReturn("FAKE_ORDER_123");

            mockMvc.perform(
                    post("/payments/takeout/temporary")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "phone_number": "01012345678",
                              "to_owner": "리뷰 이벤트 감사합니다.",
                              "total_menu_price": 24000,
                              "provide_cutlery": true,
                              "total_amount": 24000
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                      {
                        "order_id": "FAKE_ORDER_123"
                      }
                    """));

            TemporaryPayment temporaryPayment = temporaryPaymentRedisRepository.getById("FAKE_ORDER_123");
            assertSoftly(
                softly -> {
                    softly.assertThat(temporaryPayment.getPgOrderId()).isEqualTo("FAKE_ORDER_123");
                    softly.assertThat(temporaryPayment.getUserId()).isEqualTo(user.getId());
                    softly.assertThat(temporaryPayment.getOrderableShopId()).isEqualTo(orderableShop.getId());
                    softly.assertThat(temporaryPayment.getPhoneNumber()).isEqualTo("01012345678");
                    softly.assertThat(temporaryPayment.getOrderType()).isEqualTo(OrderType.TAKE_OUT);
                    softly.assertThat(temporaryPayment.getAddress()).isNull();
                    softly.assertThat(temporaryPayment.getToOwner()).isEqualTo("리뷰 이벤트 감사합니다.");
                    softly.assertThat(temporaryPayment.getToRider()).isNull();
                    softly.assertThat(temporaryPayment.getProvideCutlery()).isEqualTo(true);
                    softly.assertThat(temporaryPayment.getTotalProductPrice()).isEqualTo(24000);
                    softly.assertThat(temporaryPayment.getDeliveryFee()).isNull();
                    softly.assertThat(temporaryPayment.getTotalPrice()).isEqualTo(24000);
                    softly.assertThat(temporaryPayment.getTemporaryMenuItems().get(0).name()).isEqualTo("김밥");
                    softly.assertThat(temporaryPayment.getTemporaryMenuItems().get(0).quantity()).isEqualTo(4);
                    softly.assertThat(temporaryPayment.getTemporaryMenuItems().get(0).options()).isNull();
                }
            );
        }
    }

    @Nested
    @DisplayName("결제 API - 성공")
    class PaymentSuccess {

        @Test
        void 배달_결제_승인에_성공한다() throws Exception {
            TossPaymentConfirmResponse confirmDto = new TossPaymentConfirmResponse(
                "pay_123",
                24000,
                "DONE",
                "카드",
                "2024-01-01T10:00:00+09:00",
                "2024-01-01T10:00:05+09:00"
            );
            when(tossPaymentClient.requestConfirm(eq("pay_123"), eq("FAKE_ORDER_123"), eq(24000)))
                .thenReturn(confirmDto);
            given(pgOrderIdGenerator.generatePgOrderId()).willReturn("FAKE_ORDER_123");

            mockMvc.perform(
                    post("/payments/delivery/temporary")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "address": "충청남도 천안시 동남구 병천면 충절로 1600 은솔관 422호",
                              "phone_number": "01012345678",
                              "to_owner": "리뷰 이벤트 감사합니다.",
                              "to_rider": "문 앞에 놔주세요.",
                              "total_menu_price": 24000,
                              "delivery_tip": 0,
                              "provide_cutlery": true,
                              "total_amount": 24000
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                      {
                        "order_id": "FAKE_ORDER_123"
                      }
                    """)
                );

            mockMvc.perform(
                    post("/payments/confirm")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "payment_key": "pay_123",
                              "order_id": "FAKE_ORDER_123",
                              "amount": 24000
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                      "id": 1,
                      "delivery_address": "충청남도 천안시 동남구 병천면 충절로 1600 은솔관 422호",
                      "shop_address": "천안시 동남구 병천면 1600",
                      "to_owner": "리뷰 이벤트 감사합니다.",
                      "to_rider": "문 앞에 놔주세요.",
                      "provide_cutlery": true,
                      "amount": 24000,
                      "shop_name": "김밥천국",
                      "menus": [
                        {
                          "name": "김밥",
                          "quantity": 4,
                          "options": []
                        }
                      ],
                      "order_type": "DELIVERY",
                      "requested_at": "2024.01.01 10:00",
                      "approved_at": "2024.01.01 10:00",
                      "payment_method": "카드"
                    }
                    """)
                );

            Payment payment = paymentRepository.getById(user.getId());
            Order order = payment.getOrder();
            OrderTakeout orderTakeout = order.getOrderTakeout();
            OrderDelivery orderDelivery = order.getOrderDelivery();
            List<OrderMenu> orderMenus = orderMenuRepository.findAllByOrderId(order.getId());

            assertSoftly(
                softly -> {
                    softly.assertThat(payment.getId()).isEqualTo(1);
                    softly.assertThat(payment.getPaymentKey()).isEqualTo("pay_123");
                    softly.assertThat(payment.getAmount()).isEqualTo(24000);
                    softly.assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.DONE);
                    softly.assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);

                    softly.assertThat(order.getId()).isEqualTo(1);
                    softly.assertThat(order.getOrderType()).isEqualTo(OrderType.DELIVERY);
                    softly.assertThat(order.getPhoneNumber()).isEqualTo("01012345678");
                    softly.assertThat(order.getTotalProductPrice()).isEqualTo(24000);
                    softly.assertThat(order.getTotalPrice()).isEqualTo(24000);
                    softly.assertThat(order.getPgOrderId()).isEqualTo("FAKE_ORDER_123");

                    softly.assertThat(orderTakeout).isNull();

                    softly.assertThat(orderDelivery.getToOwner()).isEqualTo("리뷰 이벤트 감사합니다.");
                    softly.assertThat(orderDelivery.getToRider()).isEqualTo("문 앞에 놔주세요.");
                    softly.assertThat(orderDelivery.getDeliveryTip()).isEqualTo(0);
                    softly.assertThat(orderDelivery.getProvideCutlery()).isEqualTo(true);

                    softly.assertThat(orderMenus).hasSize(1);
                    softly.assertThat(orderMenus.get(0).getMenuName()).isEqualTo("김밥");
                    softly.assertThat(orderMenus.get(0).getMenuPrice()).isEqualTo(6000);
                    softly.assertThat(orderMenus.get(0).getMenuPriceName()).isEqualTo("소고기 김밥");
                    softly.assertThat(orderMenus.get(0).getId()).isEqualTo(1);
                    softly.assertThat(orderMenus.get(0).getQuantity()).isEqualTo(4);
                    softly.assertThat(orderMenus.get(0).getOrderMenuOptions()).isEmpty();
                }
            );
        }

        @Test
        void 포장_결제_승인에_성공한다() throws Exception {
            TossPaymentConfirmResponse confirmDto = new TossPaymentConfirmResponse(
                "pay_123",
                24000,
                "DONE",
                "카드",
                "2024-01-01T10:00:00+09:00",
                "2024-01-01T10:00:05+09:00"
            );
            when(tossPaymentClient.requestConfirm(eq("pay_123"), eq("FAKE_ORDER_123"), eq(24000)))
                .thenReturn(confirmDto);
            given(pgOrderIdGenerator.generatePgOrderId()).willReturn("FAKE_ORDER_123");

            mockMvc.perform(
                    post("/payments/takeout/temporary")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "phone_number": "01012345678",
                              "to_owner": "리뷰 이벤트 감사합니다.",
                              "total_menu_price": 24000,
                              "provide_cutlery": true,
                              "total_amount": 24000
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                      {
                        "order_id": "FAKE_ORDER_123"
                      }
                    """));

            mockMvc.perform(
                    post("/payments/confirm")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "payment_key": "pay_123",
                              "order_id": "FAKE_ORDER_123",
                              "amount": 24000
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                      "id": 1,
                      "delivery_address": null,
                      "shop_address": "천안시 동남구 병천면 1600",
                      "to_owner": "리뷰 이벤트 감사합니다.",
                      "to_rider": null,
                      "provide_cutlery": true,
                      "amount": 24000,
                      "shop_name": "김밥천국",
                      "menus": [
                        {
                          "name": "김밥",
                          "quantity": 4,
                          "options": []
                        }
                      ],
                      "order_type": "TAKE_OUT",
                      "requested_at": "2024.01.01 10:00",
                      "approved_at": "2024.01.01 10:00",
                      "payment_method": "카드"
                    }
                    """));

            Payment payment = paymentRepository.getById(user.getId());
            Order order = payment.getOrder();
            OrderTakeout orderTakeout = order.getOrderTakeout();
            OrderDelivery orderDelivery = order.getOrderDelivery();
            List<OrderMenu> orderMenus = orderMenuRepository.findAllByOrderId(order.getId());

            assertSoftly(
                softly -> {
                    softly.assertThat(payment.getId()).isEqualTo(1);
                    softly.assertThat(payment.getPaymentKey()).isEqualTo("pay_123");
                    softly.assertThat(payment.getAmount()).isEqualTo(24000);
                    softly.assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.DONE);
                    softly.assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);

                    softly.assertThat(order.getId()).isEqualTo(1);
                    softly.assertThat(order.getOrderType()).isEqualTo(OrderType.TAKE_OUT);
                    softly.assertThat(order.getPhoneNumber()).isEqualTo("01012345678");
                    softly.assertThat(order.getTotalProductPrice()).isEqualTo(24000);
                    softly.assertThat(order.getTotalPrice()).isEqualTo(24000);
                    softly.assertThat(order.getPgOrderId()).isEqualTo("FAKE_ORDER_123");

                    softly.assertThat(orderTakeout.getToOwner()).isEqualTo("리뷰 이벤트 감사합니다.");
                    softly.assertThat(orderTakeout.getProvideCutlery()).isEqualTo(true);

                    softly.assertThat(orderDelivery).isNull();

                    softly.assertThat(orderMenus).hasSize(1);
                    softly.assertThat(orderMenus.get(0).getMenuName()).isEqualTo("김밥");
                    softly.assertThat(orderMenus.get(0).getMenuPrice()).isEqualTo(6000);
                    softly.assertThat(orderMenus.get(0).getMenuPriceName()).isEqualTo("소고기 김밥");
                    softly.assertThat(orderMenus.get(0).getId()).isEqualTo(1);
                    softly.assertThat(orderMenus.get(0).getQuantity()).isEqualTo(4);
                    softly.assertThat(orderMenus.get(0).getOrderMenuOptions()).isEmpty();
                }
            );
        }

        @Test
        void 결제_취소에_성공한다() throws Exception {
            TossPaymentConfirmResponse confirmDto = new TossPaymentConfirmResponse(
                "pay_123",
                24000,
                "DONE",
                "카드",
                "2024-01-01T10:00:00+09:00",
                "2024-01-01T10:00:05+09:00"
            );
            when(tossPaymentClient.requestConfirm(eq("pay_123"), eq("FAKE_ORDER_123"), eq(24000)))
                .thenReturn(confirmDto);
            given(pgOrderIdGenerator.generatePgOrderId()).willReturn("FAKE_ORDER_123");

            PaymentCancelResponse cancelDto = new PaymentCancelResponse(
                "pay_123",
                "FAKE_ORDER_123",
                "CANCELED",
                List.of(new CancelInfo(
                    10000,
                    "단순 변심이에요",
                    "2024-01-01T10:00:05+10:00",
                    "txrd_123"
                ))
            );
            when(tossPaymentClient.requestCancel(eq("pay_123"), eq("단순 변심"),
                eq(paymentIdempotencyKey.getIdempotencyKey())))
                .thenReturn(cancelDto);

            mockMvc.perform(
                    post("/payments/takeout/temporary")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "phone_number": "01012345678",
                              "to_owner": "리뷰 이벤트 감사합니다.",
                              "total_menu_price": 24000,
                              "provide_cutlery": true,
                              "total_amount": 24000
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                      {
                        "order_id": "FAKE_ORDER_123"
                      }
                    """));

            mockMvc.perform(
                    post("/payments/confirm")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "payment_key": "pay_123",
                              "order_id": "FAKE_ORDER_123",
                              "amount": 24000
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                      "id": 1,
                      "delivery_address": null,
                      "shop_address": "천안시 동남구 병천면 1600",
                      "to_owner": "리뷰 이벤트 감사합니다.",
                      "to_rider": null,
                      "provide_cutlery": true,
                      "amount": 24000,
                      "shop_name": "김밥천국",
                      "menus": [
                        {
                          "name": "김밥",
                          "quantity": 4,
                          "options": []
                        }
                      ],
                      "order_type": "TAKE_OUT",
                      "requested_at": "2024.01.01 10:00",
                      "approved_at": "2024.01.01 10:00",
                      "payment_method": "카드"
                    }
                    """));

            mockMvc.perform(
                    post("/payments/1/cancel")
                        .header("Authorization", "Bearer " + token)
                        .header("Idempotency-Key", paymentIdempotencyKey.getIdempotencyKey())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "cancel_reason": "단순 변심"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                      "payment_cancels": [
                        {
                          "id": 1,
                          "cancel_reason": "단순 변심이에요",
                          "cancel_amount": 10000,
                          "canceled_at": "2024.01.01 10:00"
                        }
                      ]
                    }
                    """));

            Payment payment = paymentRepository.getById(user.getId());
            List<PaymentCancel> paymentCancels = paymentCancelRepository.findAllByPaymentId(payment.getId());

            assertSoftly(
                softly -> {
                    softly.assertThat(payment.getId()).isEqualTo(1);
                    softly.assertThat(payment.getPaymentKey()).isEqualTo("pay_123");
                    softly.assertThat(payment.getAmount()).isEqualTo(24000);
                    softly.assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
                    softly.assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);

                    softly.assertThat(paymentCancels.size()).isEqualTo(1);
                    softly.assertThat(paymentCancels.get(0).getId()).isEqualTo(1);
                    softly.assertThat(paymentCancels.get(0).getCancelReason()).isEqualTo("단순 변심이에요");
                    softly.assertThat(paymentCancels.get(0).getCancelAmount()).isEqualTo(10000);
                }
            );
        }
    }
}
