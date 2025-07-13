package opensource.alzheimerdinger.core.domain.user.ui;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.LoginRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.LoginResponse;
import opensource.alzheimerdinger.core.domain.user.application.usecase.UserAuthUseCase;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    private final UserAuthUseCase userAuthUseCase;

    /**
     *  회원가입 (보호자는 ROLE_GUARDIAN, 피보호자는 ROLE_PATIENT)
     */
    @PostMapping("/sign-up")
    public BaseResponse<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        userAuthUseCase.signUp(request);
        return BaseResponse.onSuccess();
    }

    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return BaseResponse.onSuccess(userAuthUseCase.login(request));
    }

    @DeleteMapping("/logout")
    public BaseResponse<Void> logout(HttpServletRequest request) {
        userAuthUseCase.logout(request);
        return BaseResponse.onSuccess();
    }

    @GetMapping("/test")
    public BaseResponse<String> test(@CurrentUser String userId) {
        return BaseResponse.onSuccess(userId);
    }
}
