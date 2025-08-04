package opensource.alzheimerdinger.core.domain.notification.repository;

import opensource.alzheimerdinger.core.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
