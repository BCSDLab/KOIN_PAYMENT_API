package in.koreatech.payment.client;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.koreatech.payment.client.dto.TossErrorResponse;
import in.koreatech.payment.client.dto.TossPaymentConfirmRequest;

@Component
public class TossPaymentClient {

    private final WebClient webClient;
    private final String secretKey;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(
        ObjectMapper objectMapper,
        @Value("${toss-payment.secret-key}") String secretKey
    ) {
        this.objectMapper = objectMapper;
        this.secretKey = secretKey;
        this.webClient = WebClient.builder()
            .baseUrl("https://api.tosspayments.com/v1/payments")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.AUTHORIZATION, buildAuthorizationHeader())
            .build();
    }

    public String requestConfirm(String paymentKey, String orderId, Integer amount) {
        final TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(paymentKey, orderId, amount);

        try {
            return webClient.post()
                .uri("/confirm")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        } catch (WebClientResponseException e) {
            String rawBody = e.getResponseBodyAsString();
            try {
                TossErrorResponse error = objectMapper.readValue(rawBody, TossErrorResponse.class);
                throw new RuntimeException("Toss API 오류: " + error.code() + " - " + error.message(), e);
            } catch (Exception parseException) {
                throw new RuntimeException("Toss API 오류: " + rawBody, e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Toss 결제 승인 요청 실패", e);
        }
    }

    private String buildAuthorizationHeader() {
        String encoded = Base64.getEncoder().encodeToString((secretKey).getBytes(UTF_8));
        return "Basic " + encoded;
    }
}
