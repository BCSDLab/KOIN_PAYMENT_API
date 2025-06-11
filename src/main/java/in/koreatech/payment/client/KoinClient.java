package in.koreatech.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import in.koreatech.payment.client.exception.InternalKoinErrorResponse;
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
                .build())
            .exchangeToMono(response -> {
                HttpStatus status = (HttpStatus)response.statusCode();
                if (status.is2xxSuccessful()) {
                    return Mono.empty();
                }
                return response.bodyToMono(InternalKoinErrorResponse.class)
                    .flatMap(error -> Mono.error(new InternalKoinException(
                        status,
                        error.code(),
                        error.message(),
                        error.errorTraceId()
                    )));
            });
    }
}
