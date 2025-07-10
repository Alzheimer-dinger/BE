package opensource.alzheimerdinger.core.domain.user.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.LoginRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.LoginResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.ALREADY_REGISTERED_EMAIL;
import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.LOGIN_ERROR;

@Service
@RequiredArgsConstructor
public class UserAuthUseCase {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public void signUp(SignUpRequest request) {
        if (userService.isAlreadyRegistered(request.email()))
            throw new RestApiException(ALREADY_REGISTERED_EMAIL);

        userService.signUp(request);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new RestApiException(LOGIN_ERROR);

        String accessToken = tokenProvider.createAccessToken(user.getUserId(), user.getRole());
        String refreshToken = tokenProvider.createRefreshToken(user.getUserId(), user.getRole());

        return new LoginResponse(accessToken, refreshToken);
    }
}
