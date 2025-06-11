package in.koreatech.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import in.koreatech.payment.client.dto.response.KoinErrorResponse;
import in.koreatech.payment.client.exception.InternalKoinException;
import reactor.core.publisher.Mono;

@Component
public class KoinClient {

    private final WebClient webClient;

    public KoinClient(
        @Value("${koin.api-url}") String apiUrl
    ) {
        this.webClient = WebClient.builder()
            .baseUrl(apiUrl)
            .build();
    }

    public Mono<Void> checkLogin(String accessToken) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/user/check/login")
                .queryParam("accessToken", accessToken)
                .build()
            )
            .retrieve()
            .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                clientResponse -> clientResponse.bodyToMono(KoinErrorResponse.class)
                    .map(error -> new InternalKoinException(
                        error.code(),
                        error.message(),
                        error.errorTraceId()
                    ))
            )
            .toBodilessEntity()
            .then();
    }
}
