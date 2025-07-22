package opensource.alzheimerdinger.core.domain.notification.domain.repository;

import opensource.alzheimerdinger.core.domain.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
