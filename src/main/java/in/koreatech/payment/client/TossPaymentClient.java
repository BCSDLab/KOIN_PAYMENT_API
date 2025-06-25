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

import in.koreatech.payment.client.dto.request.PaymentCancelRequest;
import in.koreatech.payment.client.dto.request.PaymentConfirmRequest;
import in.koreatech.payment.client.dto.response.PaymentCancelResponse;
import in.koreatech.payment.client.dto.response.PaymentConfirmResponse;
import in.koreatech.payment.client.exception.TossPaymentErrorCode;
import in.koreatech.payment.client.exception.TossPaymentErrorResponse;
import in.koreatech.payment.client.exception.TossPaymentException;
import in.koreatech.payment.common.exception.custom.KoinIllegalStateException;

@Component
public class TossPaymentClient {

    private static final String AUTH_PREFIX = "Basic ";
    private static final String IDEMPOTENT_KEY = "Idempotency-Key";

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

    public PaymentConfirmResponse requestConfirm(String paymentKey, String orderId, Integer amount) {
        PaymentConfirmRequest request = new PaymentConfirmRequest(paymentKey, orderId, amount);

        try {
            return webClient.post()
                .uri("/confirm")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentConfirmResponse.class)
                .block();

        } catch (WebClientResponseException e) {
            throw handleErrorResponse(e);
        } catch (Exception e) {
            throw new KoinIllegalStateException("서버 에러가 발생했습니다. 관리자에게 문의해주세요.");
        }
    }

    public PaymentCancelResponse requestCancel(String paymentKey, String cancelReason, String IdempotencyKey) {
        PaymentCancelRequest request = new PaymentCancelRequest(cancelReason);

        try {
            return webClient.post()
                .uri("/{paymentKey}/cancel", paymentKey)
                .header(IDEMPOTENT_KEY, IdempotencyKey)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentCancelResponse.class)
                .block();
        } catch (WebClientResponseException e) {
            throw handleErrorResponse(e);
        } catch (Exception e) {
            throw new KoinIllegalStateException("서버 에러가 발생했습니다. 관리자에게 문의해주세요.");
        }
    }

    private RuntimeException handleErrorResponse(WebClientResponseException e) {
        try {
            String rawBody = new String(e.getResponseBodyAsByteArray(), UTF_8);
            TossPaymentErrorResponse error = objectMapper.readValue(rawBody, TossPaymentErrorResponse.class);
            TossPaymentErrorCode tossPaymentErrorCode = TossPaymentErrorCode.fromCode(error.code());
            return TossPaymentException.of(tossPaymentErrorCode.getMessage(), tossPaymentErrorCode.getStatusCode(), tossPaymentErrorCode.getCode());
        } catch (Exception ex) {
            return new KoinIllegalStateException("서버 에러가 발생했습니다. 관리자에게 문의해주세요.");
        }
    }

    private String buildAuthorizationHeader() {
        String encoded = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(UTF_8));
        return AUTH_PREFIX + encoded;
    }
}
