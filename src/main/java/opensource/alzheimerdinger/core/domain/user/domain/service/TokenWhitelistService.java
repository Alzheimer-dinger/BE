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
public class TokenWhitelistService {
    private static final Logger log = LoggerFactory.getLogger(TokenWhitelistService.class);
    private final RedisTemplate<String, String> redisTemplate;

    private final static String whitelistPrefix = "WHITELIST:";

    public boolean isWhitelistToken(String token) {
        // 너무 잦은 호출이라면 debug 로만 남겨두고
        log.debug("[TokenWhitelistService] isWhitelistToken? token={}", token);
        String saved = redisTemplate.opsForValue().get(whitelistPrefix + token);
        boolean result = saved != null && saved.equals(token);
        log.debug("[TokenWhitelistService] whitelist check result={}", result);
        return result;
    }


    public void whitelist(String token, Duration timeout) {
        log.info("[TokenWhitelistService] adding to whitelist: token={}, ttl={}s",
                token, timeout.getSeconds());
        redisTemplate.opsForValue().set(whitelistPrefix + token, token, timeout);
    }

    public void deleteWhitelistToken(String token) {
        log.info("[TokenWhitelistService] removing from whitelist: token={}", token);
        redisTemplate.delete(whitelistPrefix + token);
    }
}
