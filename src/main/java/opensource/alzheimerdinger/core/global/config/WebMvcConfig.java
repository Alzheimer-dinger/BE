package opensource.alzheimerdinger.core.global.config;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.global.interceptor.JwtBlacklistInterceptor;
import opensource.alzheimerdinger.core.global.resolver.CurrentUserArgumentResolver;
import opensource.alzheimerdinger.core.global.security.ExcludeBlacklistPathProperties;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenProvider tokenProvider;
    private final JwtBlacklistInterceptor jwtBlacklistInterceptor;
    private final ExcludeBlacklistPathProperties excludeBlacklistPathProperties;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver(tokenProvider));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtBlacklistInterceptor)
                .excludePathPatterns(excludeBlacklistPathProperties.getExcludeAuthPaths());
        }
}
