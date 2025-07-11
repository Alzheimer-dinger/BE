package opensource.alzheimerdinger.core.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.user.domain.service.TokenLifecycleService;
import opensource.alzheimerdinger.core.global.security.ExcludeAuthPathProperties;
import opensource.alzheimerdinger.core.global.security.JwtAuthenticationFilter;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final ExcludeAuthPathProperties excludeAuthPathProperties;
    private final TokenLifecycleService tokenLifecycleService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(request -> {
            excludeAuthPathProperties.getPaths().iterator()
                    .forEachRemaining(authPath ->
                            request.requestMatchers(HttpMethod.valueOf(authPath.getMethod()), authPath.getPathPattern()).permitAll()
                    );
        });

        http.authorizeHttpRequests(request -> request
                .requestMatchers(HttpMethod.POST, "/users/token").hasRole("USER") // 토큰 재발급
                // Authenticated
                .anyRequest().authenticated()
        );

        // Session 해제
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Jwt 커스텀 필터 등록
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Token Exception Handling
        http.exceptionHandling(except -> except
                .authenticationEntryPoint((request, response, authException) -> response.sendError(response.getStatus(), "토큰 오류"))
        );

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, excludeAuthPathProperties, tokenLifecycleService);
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
