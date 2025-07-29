package opensource.alzheimerdinger.core.global.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.global.annotation.RefreshToken;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.INVALID_REFRESH_TOKEN;
import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._UNAUTHORIZED;

@RequiredArgsConstructor
public class RefreshTokenArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenProvider tokenProvider;
    private static final Logger log = LoggerFactory.getLogger(RefreshTokenArgumentResolver.class);


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean supported = parameter.getParameterAnnotation(RefreshToken.class) != null
                && String.class.isAssignableFrom(parameter.getParameterType());
        log.debug("[RefreshTokenResolver] supportsParameter={} for {}", supported, parameter.getParameterName());
        return supported;
    }

    @Override
    public String resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        log.debug("[RefreshTokenResolver] resolving refresh token argument '{}'", parameter.getParameterName());

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (request == null) {
            log.warn("[RefreshTokenResolver] HttpServletRequest is null");
            throw new RestApiException(_UNAUTHORIZED);
        }

        String token = tokenProvider.getToken(request)
                .orElseThrow(() -> {
                    log.warn("[RefreshTokenResolver] no Authorization header or Bearer token");
                    return new RestApiException(_UNAUTHORIZED);
                });
        log.debug("[RefreshTokenResolver] token extracted: {}", token);

        if (tokenProvider.isAccessToken(token)) {
            log.warn("[RefreshTokenResolver] token provided is an access token, expected refresh token");
            throw new RestApiException(INVALID_REFRESH_TOKEN);
        }

        log.info("[RefreshTokenResolver] resolved refresh token successfully");
        return token;
    }
}
