package opensource.alzheimerdinger.core.domain.user.ui;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.TokenReissueResponse;
import opensource.alzheimerdinger.core.domain.user.domain.service.TokenReissueService;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.annotation.RefreshToken;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {

    private final TokenReissueService tokenReissueService;

    @PostMapping
    public BaseResponse<TokenReissueResponse> reissue(@RefreshToken String refreshToken, @CurrentUser String userId) {
        return BaseResponse.onSuccess(tokenReissueService.reissue(refreshToken, userId));
    }
}
