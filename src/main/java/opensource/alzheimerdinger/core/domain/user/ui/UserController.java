package opensource.alzheimerdinger.core.domain.user.ui;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.domain.user.application.usecase.UserProfileUseCase;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserProfileUseCase userProfileUseCase;

    @GetMapping("/profile")
    public BaseResponse<ProfileResponse> getProfile(@CurrentUser String userId) {
        return BaseResponse.onSuccess(userProfileUseCase.findProfile(userId));
    }
}
