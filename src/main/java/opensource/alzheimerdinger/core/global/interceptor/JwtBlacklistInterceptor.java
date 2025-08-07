package opensource.alzheimerdinger.core.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.domain.service.TokenBlacklistService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.EMPTY_JWT;
import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.EXPIRED_MEMBER_JWT;

@Component
@RequiredArgsConstructor
public class JwtBlacklistInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtBlacklistInterceptor.class);
    private final TokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        log.debug("[JwtBlacklistInterceptor] incoming request: {} {}", req.getMethod(), req.getRequestURI());
        String token = tokenProvider.getToken(req)
                .orElseThrow(() -> {
                    log.warn("[JwtBlacklistInterceptor] no token found");
                    return new RestApiException(EMPTY_JWT);
                });

        boolean isBlack = tokenBlacklistService.isBlacklistToken(token);
        log.info("[JwtBlacklistInterceptor] token={} blacklisted={}", token, isBlack);
        if (isBlack) {
            throw new RestApiException(EXPIRED_MEMBER_JWT);
        }
        return true;
    }
}
