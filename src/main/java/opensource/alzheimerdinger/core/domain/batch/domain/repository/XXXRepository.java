package opensource.alzheimerdinger.core.domain.batch.domain.repository;

import opensource.alzheimerdinger.core.domain.batch.domain.entity.XXXEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface XXXRepository extends JpaRepository<XXXEntity, String> {

    Optional<XXXEntity> findById(String id);
    Page<XXXEntity> findByStatus(String status, Pageable pageable);
    Page<XXXEntity> findByCreatedDateAfter(LocalDateTime date, Pageable pageable);
    List<XXXEntity> findByIdIn(List<String> ids);
    Optional<XXXEntity> findByName(String name);
} 