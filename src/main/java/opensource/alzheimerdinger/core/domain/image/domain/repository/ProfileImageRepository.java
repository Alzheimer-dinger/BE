package opensource.alzheimerdinger.core.domain.image.domain.repository;

import opensource.alzheimerdinger.core.domain.image.domain.entity.ProfileImage;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, String> {
    Optional<ProfileImage> findByUser(User user);
}