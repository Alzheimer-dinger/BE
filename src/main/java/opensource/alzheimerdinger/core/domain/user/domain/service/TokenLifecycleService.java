package opensource.alzheimerdinger.core.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenLifecycleService {

    private final static String accessTokenPrefix = "ACCESS_TOKEN:";
    private final static String refreshTokenPrefix = "REFRESH_TOKEN:";
    private final static String blacklistPrefix = "BLACKLIST:";
    private final RedisTemplate<String, String> redisTemplate;

    public void saveAccessToken(String userId, String accessToken, Duration timeout) {
        redisTemplate.opsForValue().set(accessTokenPrefix + userId, accessToken, timeout);
    }

    public void saveRefreshToken(String userId, String refreshToken, Duration timeout) {
        redisTemplate.opsForValue().set(refreshTokenPrefix + userId, refreshToken, timeout);
    }

    public boolean existsByAccessToken(String userId, String accessToken) {
        String savedToken = redisTemplate.opsForValue().get(accessTokenPrefix + userId);

        // 존재하지 않는다면 false
        if(savedToken == null)
            return false;

        // 유저가 제공한 토큰과 매칭 검사
        return Objects.equals(savedToken, accessToken);
    }

    public boolean existsByRefreshToken(String userId, String refreshToken) {
        String savedToken = redisTemplate.opsForValue().get(refreshTokenPrefix + userId);

        // 존재하지 않는다면 false
        if(savedToken == null)
            return false;

        // 유저가 제공한 토큰과 매칭 검사
        return Objects.equals(savedToken, refreshToken);
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(refreshTokenPrefix + userId);
    }

    public void deleteAccessToken(String userId) {
        redisTemplate.delete(accessTokenPrefix + userId);
    }

    public void saveBlacklist(String userId, String accessToken) {
        redisTemplate.opsForSet().add(blacklistPrefix + accessTokenPrefix + userId, accessToken);
    }
}
