package opensource.alzheimerdinger.core.domain.user.domain.repository;

import opensource.alzheimerdinger.core.domain.user.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.user.domain.entity.RelationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationRepository extends JpaRepository<Relation, RelationId> {
}
