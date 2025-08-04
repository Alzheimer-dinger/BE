package opensource.alzheimerdinger.core.domain.notification.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.notification.entity.FcmToken;
import opensource.alzheimerdinger.core.domain.notification.repository.FcmTokenRepository;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import org.springframework.stereotype.Service;

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
}
