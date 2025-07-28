package opensource.alzheimerdinger.core.domain.notification.domain.repository;

import opensource.alzheimerdinger.core.domain.notification.domain.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, String> {
}
