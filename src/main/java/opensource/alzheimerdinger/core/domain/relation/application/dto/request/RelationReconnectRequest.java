package opensource.alzheimerdinger.core.domain.relation.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RelationReconnectRequest(
        @NotBlank String relationId,
        @NotBlank String to
) {}
