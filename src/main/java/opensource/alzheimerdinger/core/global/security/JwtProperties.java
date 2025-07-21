package opensource.alzheimerdinger.core.global.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
public class JwtProperties {
    @Value("${jwt.key}")
    private String key;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriodDay;
}