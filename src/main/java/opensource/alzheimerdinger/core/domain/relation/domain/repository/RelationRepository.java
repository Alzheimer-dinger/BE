package opensource.alzheimerdinger.core.domain.relation.domain.repository;

import opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RelationRepository extends JpaRepository<Relation, String> {

    @Query("""
        SELECT new opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse(
            relation.relationId,
            CASE WHEN :userId = patient.userId THEN guardian.name ELSE patient.name END,
            CASE WHEN :userId = patient.userId THEN guardian.patientCode ELSE patient.patientCode END,
            CASE WHEN :userId = patient.userId THEN opensource.alzheimerdinger.core.domain.user.domain.entity.Role.GUARDIAN
                 ELSE opensource.alzheimerdinger.core.domain.user.domain.entity.Role.PATIENT END,
            relation.createdAt,
            relation.relationStatus,
            relation.initiator
        )
        FROM Relation relation
        JOIN relation.patient patient
        JOIN relation.guardian guardian
        WHERE patient.userId = :userId OR guardian.userId = :userId
            AND patient.deletedAt IS NULL
        ORDER BY relation.createdAt DESC
    """)
    List<RelationResponse> findRelation(@Param("userId") String userId);

    @Query("""
    SELECT COUNT(r) > 0
    FROM Relation r
    WHERE (r.guardian = :u1 AND r.patient = :u2)
       OR (r.guardian = :u2 AND r.patient = :u1)
""")
    boolean existsByUsers(@Param("u1") User u1, @Param("u2") User u2);

    List<Relation> findByPatientAndGuardian(User patient, User guardian);
}
