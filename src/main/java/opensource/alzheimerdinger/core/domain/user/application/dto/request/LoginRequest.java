package opensource.alzheimerdinger.core.domain.user.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest (
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull String fcmToken
) {}
