package opensource.alzheimerdinger.core.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    private final static String blacklistPrefix = "BLACKLIST:";

    public boolean isBlacklistToken(String token) {
        String savedToken = redisTemplate.opsForValue().get(blacklistPrefix + token);

        // 존재하지 않는다면 false
        if(savedToken == null)
            return false;

        // 유저가 제공한 토큰과 매칭 검사
        return Objects.equals(savedToken, token);
    }

    public void blacklist(String token, Duration expiration) {
        redisTemplate.opsForValue().set(blacklistPrefix + token, "", expiration);
    }
}
