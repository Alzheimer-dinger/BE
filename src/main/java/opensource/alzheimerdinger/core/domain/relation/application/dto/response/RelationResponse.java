package opensource.alzheimerdinger.core.domain.relation.application.dto.response;

import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;

import java.time.LocalDateTime;

public record RelationResponse(
        String relationId,
        String counterId,
        String name,
        String patientCode,
        Role relationType,
        LocalDateTime createdAt,
        RelationStatus status,
        Role initiator
) {}
