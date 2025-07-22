package opensource.alzheimerdinger.core.domain.user.application.usecase;

import jakarta.servlet.http.HttpServletRequest;
import opensource.alzheimerdinger.core.domain.relation.domain.service.RelationService;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.LoginRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.LoginResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.RefreshTokenService;
import opensource.alzheimerdinger.core.domain.user.domain.service.TokenBlacklistService;
import opensource.alzheimerdinger.core.domain.user.domain.service.TokenWhitelistService;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import opensource.alzheimerdinger.core.global.util.SecureRandomGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Optional;

import static opensource.alzheimerdinger.core.domain.user.domain.entity.Gender.MALE;
import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.ALREADY_REGISTERED_EMAIL;
import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.LOGIN_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthUseCaseTest {

    @Mock UserService userService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock TokenProvider tokenProvider;
    @Mock RefreshTokenService refreshTokenService;
    @Mock SecureRandomGenerator secureRandomGenerator;
    @Mock RelationService relationService;
    @Mock TokenBlacklistService tokenBlacklistService;
    @Mock TokenWhitelistService tokenWhitelistService;

    @InjectMocks UserAuthUseCase userAuthUseCase;

    @Test
    void signUp_success_without_patient() {
        SignUpRequest req = new SignUpRequest("이름", "이메일", "비밀번호", MALE, null);
        when(userService.isAlreadyRegistered(req.email())).thenReturn(false);
        when(secureRandomGenerator.generate()).thenReturn("123456");
        User saved = mock(User.class);
        when(userService.save(req, "123456")).thenReturn(saved);

        userAuthUseCase.signUp(req);

        verify(userService).save(req, "123456");
        verifyNoInteractions(relationService);
    }

    @Test
    void signUp_fail_duplicate_email() {
        SignUpRequest req = new SignUpRequest("이름", "이메일", "비밀번호", MALE, null);
        when(userService.isAlreadyRegistered(req.email())).thenReturn(true);

        Throwable thrown = catchThrowable(() -> userAuthUseCase.signUp(req));

        assertThat(thrown)
                .isInstanceOf(RestApiException.class);

        assertThat(((RestApiException) thrown).getErrorCode())
                .isEqualTo(ALREADY_REGISTERED_EMAIL.getCode());
    }

    @Test
    void login_success() {
        LoginRequest req = new LoginRequest("이메일", "비밀번호");
        User u = mock(User.class);
        when(userService.findByEmail(req.email())).thenReturn(u);
        when(u.getPassword()).thenReturn("암호화된_비밀번호");
        when(u.getUserId()).thenReturn("1");
        when(u.getRole()).thenReturn(Role.PATIENT);
        when(passwordEncoder.matches(req.password(), "암호화된_비밀번호")).thenReturn(true);
        when(tokenProvider.createAccessToken("1", Role.PATIENT)).thenReturn("access");
        when(tokenProvider.createRefreshToken("1", Role.PATIENT)).thenReturn("refresh");
        Duration d = Duration.ofMinutes(30);
        when(tokenProvider.getRemainingDuration("refresh")).thenReturn(Optional.of(d));

        LoginResponse res = userAuthUseCase.login(req);

        assertThat(res.accessToken()).isEqualTo("access");
        assertThat(res.refreshToken()).isEqualTo("refresh");
        verify(refreshTokenService).saveRefreshToken("1", "refresh", d);
    }

    @Test
    void login_fail_bad_password() {
        LoginRequest req = new LoginRequest("이메일", "틀린_비밀번호");
        User u = mock(User.class);
        when(userService.findByEmail(req.email())).thenReturn(u);
        when(u.getPassword()).thenReturn("암호화된_비밀번호");
        when(passwordEncoder.matches(req.password(), "암호화된_비밀번호")).thenReturn(false);

        Throwable thrown = catchThrowable(() -> userAuthUseCase.login(req));

        assertThat(thrown)
                .isInstanceOf(RestApiException.class);

        assertThat(((RestApiException) thrown).getErrorCode())
                .isEqualTo(LOGIN_ERROR.getCode());
    }

    @Test
    void logout_success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(tokenProvider.getToken(request)).thenReturn(Optional.of("access"));
        when(tokenProvider.getId("access")).thenReturn(Optional.of("1"));
        Duration d = Duration.ofMinutes(30);
        when(tokenProvider.getRemainingDuration("access")).thenReturn(Optional.of(d));

        userAuthUseCase.logout(request);

        verify(tokenWhitelistService).deleteWhitelistToken("access");
        verify(refreshTokenService).deleteRefreshToken("1");
        verify(tokenBlacklistService).blacklist("access", d);
    }
}
