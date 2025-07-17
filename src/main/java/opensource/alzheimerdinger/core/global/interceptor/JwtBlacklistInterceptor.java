package opensource.alzheimerdinger.core.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.domain.service.TokenBlacklistService;
import opensource.alzheimerdinger.core.domain.user.domain.service.RefreshTokenService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.*;

@Component
@RequiredArgsConstructor
public class JwtBlacklistInterceptor implements HandlerInterceptor {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 유저 정보 조회
        String token = tokenProvider.getToken(request)
                .orElseThrow(() -> new RestApiException(EMPTY_JWT));

        // 블랙리스트 검사
        if (tokenBlacklistService.isBlacklistToken(token)) {
            throw new RestApiException(EXPIRED_MEMBER_JWT);
        }

        return true;
    }
}
