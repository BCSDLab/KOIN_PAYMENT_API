package in.koreatech.payment.acceptance.domain;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import in.koreatech.koin.domain.order.cart.model.Cart;
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
import in.koreatech.payment.acceptance.fixture.ShopFixture;
import in.koreatech.payment.acceptance.fixture.UserFixture;
import in.koreatech.payment.common.auth.JwtProvider;

public class PaymentApiTest extends AcceptanceTest {

    @Autowired
    private UserFixture userFixture;

    @Autowired
    private JwtProvider jwtProvider;

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

    private User user;
    private String token;
    private Shop shop;
    private OrderableShop orderableShop;
    private Cart cart;
    private OrderableShopMenu menuGimbap;
    private OrderableShopMenuPrice gimbapPrice;

    @BeforeAll
    void setUp() {
        user = userFixture.코인_유저();
        token = jwtProvider.createToken(user);
        shop = shopFixture.김밥천국();
        orderableShop = orderableShopFixture.주문_가능_김밥천국(shop);
        cart = cartFixture.장바구니(user, orderableShop);
        menuGimbap = orderableShopMenuFixture.주문_가능_상점_메뉴(orderableShop, "김밥");
        gimbapPrice = orderableShopMenuPriceFixture.주문_가능_상점_메뉴_가격(menuGimbap, "소고기 김밥", 6000);
        cartFixture.addOrderMenuItem(cart, menuGimbap, gimbapPrice, List.of(), 4);
    }

    @Nested
    class TemporaryPaymentSuccess {

        @Test
        void 임시_배달_결제_정보_저장에_성공한다() throws Exception {
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
                    """));
        }
    }
}
