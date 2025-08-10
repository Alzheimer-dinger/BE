package opensource.alzheimerdinger.core.domain.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Gender;

public record UpdateProfileRequest(
        @NotBlank String name,
        @NotNull Gender gender,
        String currentPassword,
        String newPassword
) {}