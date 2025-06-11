package in.koreatech.payment.common.auth;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ExtractAuthenticationInterceptor implements HandlerInterceptor {

    private static final String BEARER_TYPE = "Bearer ";
    private static final int BEARER_TYPE_LEN = 7;

    private final JwtTokenResolver jwtTokenResolver;
    private final UserIdContext userIdContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return extractAccessToken(request.getHeader(AUTHORIZATION))
            .map(token -> {
                Integer userId = jwtTokenResolver.getUserId(token);
                userIdContext.setUserId(userId);
                return true;
            })
            .orElse(false);
    }

    public Optional<String> extractAccessToken(String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(BEARER_TYPE)) {
            return Optional.of(authorizationHeader.substring(BEARER_TYPE_LEN));
        }
        return Optional.empty();
    }
}
