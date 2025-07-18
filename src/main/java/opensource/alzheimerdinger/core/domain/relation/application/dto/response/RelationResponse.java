package opensource.alzheimerdinger.core.domain.relation.application.dto.response;

import opensource.alzheimerdinger.core.domain.relation.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationType;

import java.time.LocalDateTime;

public record RelationResponse(
        String name,
        String patientCode,
        RelationType relationType,
        LocalDateTime createdAt,
        RelationStatus status
) {
    public static RelationResponse create(Relation relation) {
        return null;
    }
}
