package opensource.alzheimerdinger.core.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String TOKEN_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String ID_CLAIM = "id";
    private static final String ROLE_CLAIM = "role";

    public String createAccessToken(String id, Role role) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(Date.from(
                        LocalDateTime.now()
                                .plusDays(jwtProperties.getAccessTokenExpirationPeriodDay())
                                .atZone(ZoneId.of("Asia/Seoul"))
                                .toInstant()
                ))
                .setSubject(ACCESS_TOKEN_SUBJECT)
                .claim(ID_CLAIM, id)
                .claim(ROLE_CLAIM, role)
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String id, Role role) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(Date.from(
                        LocalDateTime.now()
                                .plusDays(jwtProperties.getAccessTokenExpirationPeriodDay())
                                .atZone(ZoneId.of("Asia/Seoul"))
                                .toInstant()
                ))
                .setSubject(REFRESH_TOKEN_SUBJECT)
                .claim(ID_CLAIM, id)
                .claim(ROLE_CLAIM, role)
                .signWith(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(jwtToken);  // Decode
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(new User(claims.get(ID_CLAIM, String.class), "", authorities), token, authorities);
    }

    public Optional<String> getId(String token) {
        try {
            return Optional.ofNullable(getClaims(token).get(ID_CLAIM, String.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Role> getRole(String token) {
        try {
            return Optional.ofNullable(getClaims(token).get(ROLE_CLAIM, Role.class));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<String> getToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(TOKEN_HEADER))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.replace(BEARER, ""));
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Optional<Date> getExpiration(String token) {
        try {
            return Optional.ofNullable(getClaims(token).getExpiration());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Duration> getRemainingDuration(String token) {
        return getExpiration(token)
                .map(date -> Duration.between(Instant.now(), date.toInstant()));
    }
}
