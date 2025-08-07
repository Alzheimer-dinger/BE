package opensource.alzheimerdinger.core.global.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._UNAUTHORIZED;

@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenProvider tokenProvider;
    private static final Logger log = LoggerFactory.getLogger(CurrentUserArgumentResolver.class);


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean supported = parameter.getParameterAnnotation(CurrentUser.class) != null
                && String.class.isAssignableFrom(parameter.getParameterType());
        log.debug("[CurrentUserResolver] supportsParameter={} for {}", supported, parameter.getParameterName());
        return supported;

    }

    @Override
    public String resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        log.debug("[CurrentUserResolver] start resolving argument '{}' of type {}",
                parameter.getParameterName(), parameter.getParameterType().getSimpleName());

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        if (request == null) {
            log.warn("[CurrentUserResolver] HttpServletRequest is null");
            throw new RestApiException(_UNAUTHORIZED);
        }

        String token = tokenProvider.getToken(request)
                .orElseThrow(() -> {
                    log.warn("[CurrentUserResolver] no Authorization header or Bearer token");
                    return new RestApiException(_UNAUTHORIZED);
                });
        log.debug("[CurrentUserResolver] token extracted: {}", token);

        String userId = tokenProvider.getId(token)
                .orElseThrow(() -> {
                    log.warn("[CurrentUserResolver] token did not contain valid userId");
                    return new RestApiException(_UNAUTHORIZED);
                });
        log.info("[CurrentUserResolver] resolved current userId={}", userId);

        return userId;
    }
}
