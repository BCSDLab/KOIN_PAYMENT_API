package in.koreatech.payment.unit.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Base64;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.koreatech.payment.client.TossPaymentClient;
import in.koreatech.payment.client.dto.response.TossPaymentConfirmResponse;
import in.koreatech.payment.client.exception.TossPaymentException;
import in.koreatech.payment.unit.support.MockHttpServer;
import okhttp3.mockwebserver.RecordedRequest;

public class TossPaymentClientTest {

    private MockHttpServer mockHttpServer;
    private TossPaymentClient tossPaymentClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockHttpServer = new MockHttpServer();
        objectMapper = new ObjectMapper();
        String baseUrl = mockHttpServer.baseUrl();
        String secretKey = "test_sk";

        tossPaymentClient = new TossPaymentClient(objectMapper, baseUrl, secretKey);
    }

    @AfterEach
    void tearDown() {
        mockHttpServer.shutdown();
    }

    @Nested
    class PaymentSuccess {

        @Test
        void 결제_승인_요청에_성공한다() throws Exception {
            // given
            TossPaymentConfirmResponse dto = new TossPaymentConfirmResponse(
                "pay_123",
                15000,
                "DONE",
                "CARD",
                "2024-01-01T10:00:00+09:00",
                "2024-01-01T10:00:05+09:00"
            );
            mockHttpServer.enqueueJson(objectMapper.writeValueAsString(dto), 200);

            // when
            TossPaymentConfirmResponse response = tossPaymentClient.requestConfirm(
                "pay_123",
                "a4CWyWY5m89PNh7xJwhk1",
                15000
            );

            // then
            assertAll(
                () -> assertThat(response.paymentKey()).isEqualTo("pay_123"),
                () -> assertThat(response.totalAmount()).isEqualTo(15000),
                () -> assertThat(response.status()).isEqualTo("DONE"),
                () -> assertThat(response.method()).isEqualTo("CARD")
            );

            RecordedRequest request = mockHttpServer.takeRequest();
            assertThat(request.getPath()).isEqualTo("/confirm");
            assertThat(request.getMethod()).isEqualTo("POST");

            assertThat(request.getHeader("Content-Type")).startsWith("application/json");
            String expectedAuth = "Basic " + Base64.getEncoder().encodeToString("test_sk:".getBytes(UTF_8));
            assertThat(request.getHeader("Authorization"))
                .isEqualTo(expectedAuth)
                .startsWith("Basic ")
                .doesNotContain("\n")
                .doesNotContain("\r");

            String body = request.getBody().readUtf8();
            assertThat(body).contains("\"paymentKey\":\"pay_123\"");
            assertThat(body).contains("\"orderId\":\"a4CWyWY5m89PNh7xJwhk1\"");
            assertThat(body).contains("\"amount\":15000");
        }
    }

    @Nested
    class PaymentFailure {

        @ParameterizedTest
        @MethodSource("invalidParams")
        void 결제_승인_요청_과정에서_필수값이_누락되면_INVALID_REQUEST_예외를_던진다(
            String paymentKey, String orderId, Integer amount
        ) throws Exception {
            // given
            String errorJson = """
                {
                  "code": "INVALID_REQUEST",
                  "message": "잘못된 요청입니다."
                }
                """;
            mockHttpServer.enqueueJson(errorJson, 400);

            // when & then
            TossPaymentException exception = assertThrows(
                TossPaymentException.class,
                () -> tossPaymentClient.requestConfirm(paymentKey, orderId, amount)
            );
            assertThat(exception.getErrorCode()).isEqualTo("INVALID_REQUEST");
            assertThat(exception).hasMessage("잘못된 요청입니다.");

            RecordedRequest request = mockHttpServer.takeRequest();
            assertThat(request.getPath()).isEqualTo("/confirm");
            assertThat(request.getMethod()).isEqualTo("POST");
        }

        static Stream<Arguments> invalidParams() {
            return Stream.of(
                Arguments.of(null, "a4CWyWY5m89PNh7xJwhk1", 99999),
                Arguments.of("pay_123", null, 99999),
                Arguments.of("pay_123", "a4CWyWY5m89PNh7xJwhk1", null)
            );
        }
    }
}
