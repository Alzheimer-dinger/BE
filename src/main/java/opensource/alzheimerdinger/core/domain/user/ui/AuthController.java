package opensource.alzheimerdinger.core.domain.user.ui;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.request.SignUpRequest;
import opensource.alzheimerdinger.core.domain.user.application.usecase.UserAuthUseCase;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    private final UserAuthUseCase userAuthUseCase;

    @PostMapping("/sign-up")
    public BaseResponse<Void> signUp(SignUpRequest request) {
        userAuthUseCase.signUp(request);
        return BaseResponse.onSuccess();
    }
}
