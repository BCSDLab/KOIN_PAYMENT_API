package in.koreatech.payment.client;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.koreatech.payment.client.dto.TossErrorResponse;
import in.koreatech.payment.client.dto.TossPaymentConfirmRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TossPaymentClient {

    private static final String AUTH_PREFIX = "Basic ";

    private final WebClient webClient;
    private final String secretKey;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(
        ObjectMapper objectMapper,
        @Value("${toss-payment.api-base-url}") String baseUrl,
        @Value("${toss-payment.secret-key}") String secretKey
    ) {
        this.secretKey = secretKey;
        this.objectMapper = objectMapper;
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .defaultHeader(AUTHORIZATION, buildAuthorizationHeader())
            .build();
    }

    public void requestConfirm(String paymentKey, String orderId, Integer amount) {
        TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(paymentKey, orderId, amount);

        try {
            webClient.post()
                .uri("/confirm")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        } catch (WebClientResponseException e) {
            throw handleErrorResponse(e);
        } catch (Exception e) {
            throw new RuntimeException("Toss 결제 승인 요청 실패");
        }
    }

    // TODO. 패키지 정리 이후 커스텀 예외 처리
    private RuntimeException handleErrorResponse(WebClientResponseException e) {
        try {
            String rawBody = new String(e.getResponseBodyAsByteArray(), UTF_8);
            TossErrorResponse error = objectMapper.readValue(rawBody, TossErrorResponse.class);
            log.error("[Toss Payments 오류] code: {}, message: {}", error.code(), error.message());
            return new RuntimeException("Toss 결제 승인 요청 실패");
        } catch (Exception ex) {
            return new RuntimeException("Toss 결제 승인 요청 실패");
        }
    }

    private String buildAuthorizationHeader() {
        String encoded = Base64.getEncoder().encodeToString((secretKey).getBytes(UTF_8));
        return AUTH_PREFIX + encoded;
    }
}
