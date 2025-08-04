package opensource.alzheimerdinger.core.domain.user.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.TokenReissueResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.EXPIRED_MEMBER_JWT;
import static opensource.alzheimerdinger.core.global.exception.code.status.AuthErrorStatus.INVALID_REFRESH_TOKEN;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenReissueService {

    private static final Logger log = LoggerFactory.getLogger(TokenReissueService.class);
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public TokenReissueResponse reissue(String refreshToken, String userId) {
        log.info("[TokenReissueService] reissue start: userId={} refreshToken={}", userId, refreshToken);

        // 존재 유무 검사
        if (!refreshTokenService.isExist(refreshToken, userId)) {
            log.warn("[TokenReissueService] invalid refresh token for userId={}", userId);
            throw new RestApiException(INVALID_REFRESH_TOKEN);
        }

        // 기존에 있는 토큰 삭제
        refreshTokenService.deleteRefreshToken(userId);
        log.debug("[TokenReissueService] old refresh token deleted: userId={}", userId);

        // 새 토큰 발급
        User user = userService.findUser(userId);
        String newAccessToken = tokenProvider.createAccessToken(userId, user.getRole());
        String newRefreshToken = tokenProvider.createRefreshToken(userId, user.getRole());
        Duration duration = tokenProvider.getRemainingDuration(refreshToken)
                .orElseThrow(() -> {
                    log.error("[TokenReissueService] expired refresh token for userId={}", userId);
                    return new RestApiException(EXPIRED_MEMBER_JWT);
                });
        log.info("[TokenReissueService] new tokens created: userId={} accessTokenLen={} refreshTokenLen={}",
                userId, newAccessToken.length(), newRefreshToken.length());

        // 저장
        refreshTokenService.saveRefreshToken(userId, newRefreshToken, duration);
        log.debug("[TokenReissueService] new refresh token saved: userId={} expiresIn={}s",
                userId, duration.getSeconds());

        return new TokenReissueResponse(newAccessToken, newRefreshToken);
    }
}
