package opensource.alzheimerdinger.core.domain.user.ui;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.LoginRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.LoginResponse;
import opensource.alzheimerdinger.core.domain.user.application.usecase.UserAuthUseCase;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    private final UserAuthUseCase userAuthUseCase;

    @PostMapping("/sign-up")
    public BaseResponse<Void> signUp(@RequestBody @Valid SignUpRequest request) {
        userAuthUseCase.signUp(request);
        return BaseResponse.onSuccess();
    }

    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return BaseResponse.onSuccess(userAuthUseCase.login(request));
    }

    @GetMapping("/test")
    public BaseResponse<Void> test() {
        return BaseResponse.onSuccess();
    }
}
