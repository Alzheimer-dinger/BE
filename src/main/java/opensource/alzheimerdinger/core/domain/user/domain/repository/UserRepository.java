package opensource.alzheimerdinger.core.domain.user.domain.repository;

import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("select count(u) > 0 from User u where u.email = :email")
    Boolean existsByEmail(@Param("email") String email);

}
