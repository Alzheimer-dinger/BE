package opensource.alzheimerdinger.core.domain.user.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.domain.user.application.usecase.UserProfileUseCase;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 프로필 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserProfileUseCase userProfileUseCase;

    @Operation(
            summary = "프로필 조회",
            description = "현재 로그인한 사용자의 프로필 정보를 조회",
            responses = @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class)))
    )
    @GetMapping("/profile")
    public BaseResponse<ProfileResponse> getProfile(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(userProfileUseCase.findProfile(userId));
    }
}
