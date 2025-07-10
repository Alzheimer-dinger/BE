package opensource.alzheimerdinger.core.domain.user.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;

public record SignUpRequest(
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull Role role
) {}
