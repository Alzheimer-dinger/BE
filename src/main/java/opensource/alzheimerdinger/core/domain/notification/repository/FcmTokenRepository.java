package opensource.alzheimerdinger.core.domain.notification.repository;

import opensource.alzheimerdinger.core.domain.notification.entity.FcmToken;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, String> {

    @Modifying
    @Query("DELETE FROM FcmToken ft WHERE ft.user.userId = :userId")
    void expire(String userId);

    Optional<FcmToken> findByUser(User user);
}
