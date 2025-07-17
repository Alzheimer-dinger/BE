package opensource.alzheimerdinger.core.domain.user.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpToPatientRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {}
