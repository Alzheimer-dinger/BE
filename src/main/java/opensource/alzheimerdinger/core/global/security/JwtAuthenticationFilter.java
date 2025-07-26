package opensource.alzheimerdinger.core.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.domain.service.RefreshTokenService;
import opensource.alzheimerdinger.core.domain.user.domain.service.TokenWhitelistService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.pattern.PathPatternParser;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;

import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.*;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final ExcludeAuthPathProperties excludeAuthPathProperties;
    private final RefreshTokenService refreshTokenService;
    private final TokenWhitelistService tokenWhitelistService;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final PathPatternParser pathPatternParser = new PathPatternParser();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("[JwtAuthFilter] start: {} {}", request.getMethod(), request.getRequestURI());
        try {
            if (isExcludedPath(request)) {
                log.debug("[JwtAuthFilter] excluded path, skip auth");
                filterChain.doFilter(request, response);
                return;
            }

            String token = tokenProvider.getToken(request)
                    .orElseThrow(() -> {
                        log.warn("[JwtAuthFilter] missing Authorization header");
                        return new RestApiException(EMPTY_JWT);
                    });

            // 토큰 캐시 확인
            if (tokenWhitelistService.isWhitelistToken(token)) {
                log.debug("[JwtAuthFilter] token whitelisted");
                setAuthentication(token);
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 검증
            if (tokenProvider.validateToken(token)) {
                log.info("[JwtAuthFilter] token valid, authenticating user");
                setAuthentication(token);
                tokenWhitelistService.whitelist(token, Duration.ofSeconds(30));
            } else {
                log.warn("[JwtAuthFilter] invalid token");
                throw new RestApiException(INVALID_ACCESS_TOKEN);
            }

            // 토큰 캐시
            tokenWhitelistService.whitelist(token, Duration.ofSeconds(30));

            filterChain.doFilter(request, response);
        } catch (RestApiException e) {
            log.error("[JwtAuthFilter] authentication error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");

            String jsonResponse = String.format("{\"message\": \"%s\"}", e.getMessage());

            PrintWriter writer = response.getWriter();
            writer.write(jsonResponse);
            writer.flush();
            writer.close();
        }
    }

    public boolean isExcludedPath(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        HttpMethod requestMethod = HttpMethod.valueOf(request.getMethod());

        return excludeAuthPathProperties.getPaths().stream()
                .anyMatch(authPath ->
                        pathPatternParser.parse(authPath.getPathPattern())
                                .matches(PathContainer.parsePath(requestPath))
                        && requestMethod.equals(HttpMethod.valueOf(authPath.getMethod()))
                );
    }

    private void setAuthentication(String token) {
        if (tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
