package opensource.alzheimerdinger.core.domain.notification.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.notification.entity.FcmToken;
import opensource.alzheimerdinger.core.domain.notification.repository.FcmTokenRepository;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.stereotype.Service;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus.FCM_TOKEN_NOT_FOUND;
import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    public void upsert(User user, String token) {
        FcmToken fcmToken = fcmTokenRepository.findByUser(user)
                .orElseGet(() -> FcmToken.builder()
                        .token(token)
                        .user(user)
                        .build());

        fcmToken.updateToken(token);
        fcmTokenRepository.save(fcmToken);
    }

    public void expire(String userId) {
        fcmTokenRepository.expire(userId);
    }

    public String findByUser(User user) {
        return fcmTokenRepository.findTokenByUser(user)
                .orElseThrow(() -> new RestApiException(FCM_TOKEN_NOT_FOUND));
    }
}
