package in.koreatech.payment.common.auth;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import in.koreatech.payment.common.auth.exception.UnauthenticatedTokenException;
import in.koreatech.payment.common.exception.custom.AuthenticationException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccessTokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final AccessTokenContext accessTokenContext;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AccessToken.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        String accessToken = accessTokenContext.getAccessToken();
        if (accessToken == null || accessToken.isBlank()) {
            throw UnauthenticatedTokenException.withDetail("accessToken: " + accessToken);
        }
        return accessToken;
    }
}
