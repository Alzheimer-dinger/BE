package opensource.alzheimerdinger.core.domain.notification.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.notification.entity.Notification;
import opensource.alzheimerdinger.core.domain.notification.repository.NotificationRepository;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserService userService;
    private final NotificationRepository notificationRepository;

    public String sendNotification(String token, String title, String body, String userId) {
        com.google.firebase.messaging.Notification fcmNotification = com.google.firebase.messaging.Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        WebpushConfig webPush = WebpushConfig.builder()
                .putHeader("TTL", "86400")
                .setNotification(new WebpushNotification(
                        title,
                        body,
                        "https://api.alzheimerdinger.com/assets/logo.png"
                ))
                .setFcmOptions(WebpushFcmOptions.withLink("https://www.alzheimerdinger.com/call"))
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(webPush)
                .build();

        User user = userService.findUser(userId);

        Notification notification = Notification.builder()
                .title(title)
                .body(body)
                .user(user)
                .build();

        notificationRepository.save(notification);

        try {
            return FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode().equals(MessagingErrorCode.INVALID_ARGUMENT)) {
                // 토큰이 유효하지 않은 경우, 오류 코드를 반환
                return e.getMessagingErrorCode().toString();
            } else if (e.getMessagingErrorCode().equals(MessagingErrorCode.UNREGISTERED)) {
                // 재발급된 이전 토큰인 경우, 오류 코드를 반환
                return e.getMessagingErrorCode().toString();
            } else { // 그 외, 오류는 런타임 예외로 처리
                throw new RuntimeException(e);
            }
        }
    }
}
