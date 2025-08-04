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
public class TokenBlacklistService {
    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);
    private final RedisTemplate<String, String> redisTemplate;
    private final static String blacklistPrefix = "BLACKLIST:";

    public boolean isBlacklistToken(String token) {
        log.debug("[TokenBlacklistService] isBlacklistToken: token={}", token);

        String savedToken = redisTemplate.opsForValue().get(blacklistPrefix + token);
        boolean blacklisted = savedToken != null && Objects.equals(savedToken, token);

        log.info("[TokenBlacklistService] blacklisted={}", blacklisted);
        return blacklisted;
    }

    public void blacklist(String token, Duration expiration) {
        redisTemplate.opsForValue().set(blacklistPrefix + token, token, expiration);
        log.info("[TokenBlacklistService] blacklist: token={} expiration={}s", token, expiration.getSeconds());
    }
}
