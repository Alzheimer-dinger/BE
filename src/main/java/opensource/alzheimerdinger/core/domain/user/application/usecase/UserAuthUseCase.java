package opensource.alzheimerdinger.core.domain.user.application.usecase;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
import opensource.alzheimerdinger.core.domain.relation.domain.service.RelationService;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.LoginRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.LoginResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.*;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import opensource.alzheimerdinger.core.global.util.SecureRandomGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(UserAuthUseCase.class);

    public void signUp(SignUpRequest request) {
        log.debug("[UserAuth] signUp start: email={}", request.email());
        if (userService.isAlreadyRegistered(request.email())) {
            log.warn("[UserAuth] signUp failed: email already registered");
            throw new RestApiException(ALREADY_REGISTERED_EMAIL);
        }

        String code = secureRandomGenerator.generate();
        User user = userService.save(request, code);

        if (request.patientCode() != null) {
            User patient = userService.findPatient(request.patientCode());
            if (!Objects.equals(patient.getPatientCode(), request.patientCode())) {
                throw new RestApiException(_NOT_FOUND);
            }
            relationService.save(patient, user, RelationStatus.REQUESTED, Role.GUARDIAN);
        }

        log.info("[UserAuth] signUp success: userId={}", user.getUserId());
    }

    public LoginResponse login(LoginRequest request) {
        log.debug("[UserAuth] login start: email={}", request.email());
        User user = userService.findByEmail(request.email());
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RestApiException(LOGIN_ERROR);
        }

        String accessToken = tokenProvider.createAccessToken(user.getUserId(), user.getRole());
        String refreshToken = tokenProvider.createRefreshToken(user.getUserId(), user.getRole());

        Duration tokenExpiration = tokenProvider.getRemainingDuration(refreshToken)
                .orElseThrow(() -> new RestApiException(EXPIRED_MEMBER_JWT));
        refreshTokenService.saveRefreshToken(user.getUserId(), refreshToken, tokenExpiration);

        log.info("[UserAuth] login success: userId={}", user.getUserId());
        return new LoginResponse(accessToken, refreshToken);
    }

    public void logout(HttpServletRequest request) {
        log.debug("[UserAuth] logout start");
        String accessToken = tokenProvider.getToken(request)
                .orElseThrow(() -> new RestApiException(EMPTY_JWT));
        String userId = tokenProvider.getId(accessToken)
                .orElseThrow(() -> new RestApiException(INVALID_ID_TOKEN));
        Duration expiration = tokenProvider.getRemainingDuration(accessToken)
                .orElseThrow(() -> new RestApiException(INVALID_ACCESS_TOKEN));

        tokenWhitelistService.deleteWhitelistToken(accessToken);
        refreshTokenService.deleteRefreshToken(userId);
        tokenBlacklistService.blacklist(accessToken, expiration);

        log.info("[UserAuth] logout success: userId={}", userId);
    }
}
