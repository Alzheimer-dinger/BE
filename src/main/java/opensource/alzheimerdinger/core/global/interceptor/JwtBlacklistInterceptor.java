package opensource.alzheimerdinger.core.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.domain.service.TokenLifecycleService;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.*;

@Component
@RequiredArgsConstructor
public class JwtBlacklistInterceptor implements HandlerInterceptor {

    private final TokenProvider tokenProvider;
    private final TokenLifecycleService tokenLifecycleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 유저 정보 조회
        String token = tokenProvider.getToken(request)
                .orElseThrow(() -> new RestApiException(EMPTY_JWT));

        String userId = tokenProvider.getId(token)
                .orElseThrow(() -> new RestApiException(INVALID_ID_TOKEN));

        // 블랙리스트 검사
        if (tokenLifecycleService.isBlacklistToken(userId, token)) {
            throw new RestApiException(EXPIRED_MEMBER_JWT);
        }

        return true;
    }
}
