package opensource.alzheimerdinger.core.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.domain.service.TokenLifecycleService;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class JwtBlacklistInterceptor implements HandlerInterceptor {

    private final TokenProvider tokenProvider;
    private final TokenLifecycleService tokenLifecycleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = tokenProvider.getToken(request)
                .orElse(null);

        String userId = tokenProvider.getId(token)
                .orElse(null);

        if(token == null && userId == null) {
            response.setStatus(SC_UNAUTHORIZED);
            return false;
        }

        if (tokenLifecycleService.isBlacklistToken(userId, token)) {
            response.setStatus(SC_UNAUTHORIZED);
            return false;
        }

        return true;
    }
}
