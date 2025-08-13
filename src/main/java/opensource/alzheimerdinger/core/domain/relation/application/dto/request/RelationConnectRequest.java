package opensource.alzheimerdinger.core.domain.relation.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RelationConnectRequest (
        String patientCode
) {}
