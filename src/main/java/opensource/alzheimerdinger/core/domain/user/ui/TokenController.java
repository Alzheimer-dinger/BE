package opensource.alzheimerdinger.core.domain.user.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.TokenReissueResponse;
import opensource.alzheimerdinger.core.domain.user.domain.service.TokenReissueService;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.annotation.RefreshToken;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Token", description = "JWT 토큰 재발급 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
@SecurityRequirement(name = "Bearer Authentication")
public class TokenController {

    private final TokenReissueService tokenReissueService;

    @Operation(
            summary = "JWT 토큰 재발급",
            description = "Refresh Token을 사용하여 새로운 Access Token을 발급",
            responses = @ApiResponse(responseCode = "200", description = "재발급 성공",
                    content = @Content(schema = @Schema(implementation = TokenReissueResponse.class)))
    )
    @PostMapping
    public BaseResponse<TokenReissueResponse> reissue(
            @Parameter(description = "Refresh Token", required = true) @RefreshToken String refreshToken,
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(tokenReissueService.reissue(refreshToken, userId));
    }
}