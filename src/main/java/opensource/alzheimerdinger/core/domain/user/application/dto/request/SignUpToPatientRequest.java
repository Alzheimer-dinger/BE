package opensource.alzheimerdinger.core.domain.user.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Gender;

public record SignUpToPatientRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull Gender gender
) {}
