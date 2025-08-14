package in.koreatech.payment.unit.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.koreatech.payment.client.TossPaymentClient;
import in.koreatech.payment.client.dto.response.TossPaymentConfirmResponse;
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
        void 결제를_승인한다() throws Exception {
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

            String expectedAuth = "Basic " + Base64.getEncoder()
                .encodeToString("test_sk:".getBytes(StandardCharsets.UTF_8));
            assertThat(request.getHeader("Authorization")).isEqualTo(expectedAuth);
        }
    }
}
