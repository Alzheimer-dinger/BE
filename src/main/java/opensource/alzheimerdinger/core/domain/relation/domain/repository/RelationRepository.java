package opensource.alzheimerdinger.core.domain.relation.domain.repository;

import opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RelationRepository extends JpaRepository<Relation, String> {

    @Query("""
        SELECT new opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse(
            CASE WHEN :userNo = patient.userId THEN guardian.userId ELSE patient.userId END,
            CASE WHEN :userNo = patient.userId THEN guardian.name ELSE patient.name END,
            CASE WHEN :userNo = patient.userId THEN guardian.patientCode ELSE patient.patientCode END,
            CASE WHEN :userNo = patient.userId THEN opensource.alzheimerdinger.core.domain.user.domain.entity.Role.GUARDIAN
                 ELSE opensource.alzheimerdinger.core.domain.user.domain.entity.Role.PATIENT END,
            relation.createdAt,
            relation.relationStatus,
            relation.initiator
        )
        FROM Relation relation
        JOIN relation.patient patient
        JOIN relation.guardian guardian
        WHERE patient.userId = :userNo OR guardian.userId = :userNo
    """)
    List<RelationResponse> findRelation(@Param("userNo") String userNo);

}
