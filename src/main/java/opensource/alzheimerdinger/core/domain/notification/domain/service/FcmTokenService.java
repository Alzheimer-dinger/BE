package opensource.alzheimerdinger.core.domain.notification.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.notification.domain.entity.FcmToken;
import opensource.alzheimerdinger.core.domain.notification.domain.repository.FcmTokenRepository;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserService userService;

    public void save(String userId, String token) {
        User user = userService.findUser(userId);

        FcmToken fcmToken = FcmToken.builder()
                .token(token)
                .user(user)
                .build();

        fcmTokenRepository.save(fcmToken);
    }
}
