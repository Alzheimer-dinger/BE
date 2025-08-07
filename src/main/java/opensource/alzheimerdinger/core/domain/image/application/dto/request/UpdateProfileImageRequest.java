package opensource.alzheimerdinger.core.domain.image.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileImageRequest(
        @NotBlank String fileKey
) {}