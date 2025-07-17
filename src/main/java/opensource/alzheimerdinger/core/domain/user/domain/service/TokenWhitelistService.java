package opensource.alzheimerdinger.core.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TokenWhitelistService {

    private final RedisTemplate<String, String> redisTemplate;

    private final static String whitelistPrefix = "WHITELIST:";

    public boolean isWhitelistToken(String token) {
        String savedToken = redisTemplate.opsForValue().get(whitelistPrefix + token);

        // 존재하지 않는다면 false
        if(savedToken == null)
            return false;

        // 유저가 제공한 토큰과 매칭 검사
        return Objects.equals(savedToken, token);
    }


    public void whitelist(String token, Duration timeout) {
        redisTemplate.opsForValue().set(whitelistPrefix + token, "", timeout);
    }

    public void deleteWhitelistToken(String token) {
        redisTemplate.delete(whitelistPrefix + token);
    }
}
