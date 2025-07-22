package opensource.alzheimerdinger.core.domain.notification.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.notification.domain.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;


}
