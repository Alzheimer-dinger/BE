package opensource.alzheimerdinger.core.domain.user.application.usecase;

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

    public void signUp(SignUpRequest request) {
        // 이미 가입된 이메일인지 확인
        if (userService.isAlreadyRegistered(request.email()))
            throw new RestApiException(ALREADY_REGISTERED_EMAIL);

        String code = secureRandomGenerator.generate();

        // DB에 저장
        User user = userService.save(request, code);

        if(request.patientCode() != null) {
            User patient = userService.findPatient(request.patientCode());

            if(!Objects.equals(patient.getPatientCode(), request.patientCode()))
                throw new RestApiException(_NOT_FOUND);

            relationService.save(patient, user, RelationStatus.REQUESTED, Role.GUARDIAN);
        }

    }

    public LoginResponse login(LoginRequest request) {
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
    }

    public void logout(HttpServletRequest request) {
        // 회원 정보 조회
        String accessToken = tokenProvider.getToken(request)
                .orElseThrow(() -> new RestApiException(EMPTY_JWT));

        String userId = tokenProvider.getId(accessToken)
                .orElseThrow(() -> new RestApiException(INVALID_ID_TOKEN));

        Duration expiration = tokenProvider.getRemainingDuration(accessToken)
                .orElseThrow(() -> new RestApiException(INVALID_ACCESS_TOKEN));

        // 캐싱 및 저장된 토큰 삭제 후 기존 사용하던 엑세스 토큰 무효화 등록
        tokenWhitelistService.deleteWhitelistToken(accessToken);
        refreshTokenService.deleteRefreshToken(userId);
        tokenBlacklistService.blacklist(accessToken, expiration);
    }
}
