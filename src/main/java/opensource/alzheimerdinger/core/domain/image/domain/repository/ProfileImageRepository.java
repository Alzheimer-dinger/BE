package opensource.alzheimerdinger.core.domain.image.domain.repository;

import opensource.alzheimerdinger.core.domain.image.domain.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, String> {
    Optional<ProfileImage> findByUserId(String userId);
}