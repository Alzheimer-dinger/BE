package opensource.alzheimerdinger.core.domain.user.application.usecase;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
import opensource.alzheimerdinger.core.domain.relation.domain.service.RelationService;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.LoginRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpToGuardianRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.LoginResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.*;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import opensource.alzheimerdinger.core.global.util.SecureRandomGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.*;
import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthUseCase {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final SecureRandomGenerator secureRandomGenerator;
    private final RelationService relationService;
    private final TokenBlacklistService tokenBlacklistService;
    private final TokenWhitelistService tokenWhitelistService;
    private final MeterRegistry registry;

    public void signUp(SignUpRequest request) {
        // 호출 횟수
        registry.counter("domain_user_signup_requests").increment();
        // 실행 시간
        registry.timer("domain_user_signup_duration", "domain", "user")
                .record(() -> {
                    // 기존 sign-up 로직
                    if (userService.isAlreadyRegistered(request.email()))
                        throw new RestApiException(ALREADY_REGISTERED_EMAIL);

                    String code = secureRandomGenerator.generate();
                    User user = userService.save(request, code);

                    if (request.patientCode() != null) {
                        User patient = userService.findPatient(request.patientCode());
                        relationService.save(patient, user, RelationStatus.REQUESTED, Role.GUARDIAN);
                    }
                });
    }

    public LoginResponse login(LoginRequest request) {
        // 호출 횟수
        registry.counter("domain_user_login_requests").increment();
        // 실행 시간
        return registry.timer("domain_user_login_duration", "domain", "user")
                .record(() -> {
                    // 이메일로 유저 객체 조회
                    User user = userService.findByEmail(request.email());

                    // 해시로 암호화한 비밀번호와 매칭 검사
                    if (!passwordEncoder.matches(request.password(), user.getPassword()))
                        throw new RestApiException(LOGIN_ERROR);

                    // 토큰 발행
                    String accessToken = tokenProvider.createAccessToken(user.getUserId(), user.getRole());
                    String refreshToken = tokenProvider.createRefreshToken(user.getUserId(), user.getRole());

                    // refreshToken Redis에 저장
                    Duration tokenExpiration = tokenProvider.getRemainingDuration(refreshToken)
                            .orElseThrow(() -> new RestApiException(EXPIRED_MEMBER_JWT));
                    refreshTokenService.saveRefreshToken(user.getUserId(), refreshToken, tokenExpiration);

                    return new LoginResponse(accessToken, refreshToken);
                });

    }

    public void logout(HttpServletRequest request) {
        // 호출 횟수
        registry.counter("domain_user_logout_requests").increment();
        // 실행 시간
        registry.timer("domain_user_logout_duration", "domain", "user")
                .record(() -> {
                    String accessToken = tokenProvider.getToken(request)
                            .orElseThrow(() -> new RestApiException(EMPTY_JWT));
                    String userId = tokenProvider.getId(accessToken)
                            .orElseThrow(() -> new RestApiException(INVALID_ID_TOKEN));
                    Duration expiration = tokenProvider.getRemainingDuration(accessToken)
                            .orElseThrow(() -> new RestApiException(INVALID_ACCESS_TOKEN));

                    tokenWhitelistService.deleteWhitelistToken(accessToken);
                    refreshTokenService.deleteRefreshToken(userId);
                    tokenBlacklistService.blacklist(accessToken, expiration);
                });
    }
}
