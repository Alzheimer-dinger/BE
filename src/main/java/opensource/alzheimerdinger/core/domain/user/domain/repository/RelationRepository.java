package opensource.alzheimerdinger.core.domain.user.domain.repository;

import opensource.alzheimerdinger.core.domain.relation.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationId;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RelationRepository extends JpaRepository<Relation, RelationId> {

    @Query("select r from Relation r where r.patient = :user or r.guardian = :user order by r.createdAt")
    List<Relation> findRelation(@Param("user") User user);
}
