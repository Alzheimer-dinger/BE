package opensource.alzheimerdinger.core.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.TokenReissueResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.EXPIRED_MEMBER_JWT;
import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.INVALID_REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class TokenReissueService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public TokenReissueResponse reissue(String refreshToken, String userId) {
        // 존재 유무 검사
        if (refreshTokenService.isExist(refreshToken, userId))
            throw new RestApiException(INVALID_REFRESH_TOKEN);

        // 기존에 있는 토큰 삭제
        refreshTokenService.deleteRefreshToken(userId);

        // 새 토큰 발급
        User user = userService.findUser(userId);
        String newAccessToken = tokenProvider.createAccessToken(userId, user.getRole());
        String newRefreshToken = tokenProvider.createRefreshToken(userId, user.getRole());
        Duration duration = tokenProvider.getRemainingDuration(refreshToken)
                .orElseThrow(() -> new RestApiException(EXPIRED_MEMBER_JWT));

        // 저장
        refreshTokenService.saveRefreshToken(userId, newRefreshToken, duration);

        return new TokenReissueResponse(newAccessToken, newRefreshToken);
    }
}
