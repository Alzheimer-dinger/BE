package opensource.alzheimerdinger.core.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);
    private final static String refreshTokenPrefix = "REFRESH_TOKEN:";
    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String userId, String refreshToken, Duration timeout) {
        log.info("[RefreshTokenService] saveRefreshToken: userId={} expiresIn={}s", userId, timeout.getSeconds());
        redisTemplate.opsForValue().set(refreshTokenPrefix + userId, refreshToken, timeout);
    }

    public void deleteRefreshToken(String userId) {
        log.info("[RefreshTokenService] deleteRefreshToken: userId={}", userId);
        redisTemplate.delete(refreshTokenPrefix + userId);
    }

    public String findByUserId(String userId) {
        log.debug("[RefreshTokenService] findByUserId: userId={}", userId);
        return redisTemplate.opsForValue().get(refreshTokenPrefix + userId);
    }
    public boolean isExist(String token, String userId) {
        log.debug("[RefreshTokenService] isExist: userId={} token={}", userId, token);

        String savedToken = redisTemplate.opsForValue().get(refreshTokenPrefix + userId);
        boolean exists = savedToken != null && Objects.equals(savedToken, token);

        log.info("[RefreshTokenService] tokenExistence: userId={} exists={}", userId, exists);
        return exists;
    }
}
