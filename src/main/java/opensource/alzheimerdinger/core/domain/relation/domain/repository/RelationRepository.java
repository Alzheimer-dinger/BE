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
          counter.userId,
          counter.name,
          counter.patientCode,
          counter.role,
          relation.createdAt,
          relation.relationStatus,
          (relation.initiator = :userId),
          pi.fileKey
        )
        FROM Relation relation
        JOIN relation.patient patient
        JOIN relation.guardian guardian
        JOIN User counter
          ON (counter = patient OR counter = guardian) AND counter.userId <> :userId
        LEFT JOIN ProfileImage pi
          ON pi.user = counter
        WHERE (patient.userId = :userId OR guardian.userId = :userId)
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

    @Query("""
        SELECT r
        FROM Relation r
        WHERE (r.guardian = :u1 AND r.patient = :u2)
            OR (r.guardian = :u2 AND r.patient = :u1)
        ORDER BY r.createdAt DESC
    """)
    Optional<Relation> findByPatientAndGuardian(@Param("u1") User u1, @Param("u2") User u2);
}
