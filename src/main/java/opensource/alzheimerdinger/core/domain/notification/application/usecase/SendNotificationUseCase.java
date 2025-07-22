package opensource.alzheimerdinger.core.domain.notification.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.notification.domain.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendNotificationUseCase {

    private final NotificationService notificationService;

    public void send(String token, String content) {
//        notificationService
    }
}
