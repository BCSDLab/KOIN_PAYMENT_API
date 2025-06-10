package in.koreatech.payment.config;

import static io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    private final String serverUrl;

    public SwaggerConfig(
        @Value("${swagger.server-url}") String serverUrl
    ) {
        this.serverUrl = serverUrl;
    }

    @Bean
    public OpenAPI openAPI() {
        final String SECURITY_SCHEME_NAME = "Jwt Authentication";

        return new OpenAPI()
            .openapi("3.1.0")
            .info(apiInfo())
            .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
            .components(new Components().addSecuritySchemes(
                SECURITY_SCHEME_NAME,
                new SecurityScheme()
                    .name(SECURITY_SCHEME_NAME)
                    .type(HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("토큰값을 입력하여 인증을 활성화할 수 있습니다.")
            ))
            .addServersItem(new Server().url(serverUrl));
    }

    private Info apiInfo() {
        return new Info()
            .title("KOIN PAYMENT API")
            .description("KOIN PAYMENT API 문서입니다.")
            .version(LocalDate.now().toString());
    }
}
