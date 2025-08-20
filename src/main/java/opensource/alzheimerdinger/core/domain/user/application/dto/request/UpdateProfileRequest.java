package opensource.alzheimerdinger.core.domain.user.application.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Gender;

public record UpdateProfileRequest(
        @NotBlank String name,
        @NotNull Gender gender,
        String currentPassword,
        String newPassword
) {
    @AssertTrue(message = "currentPassword is required when newPassword is provided")
    public boolean isPasswordChangeValid() {
        if (newPassword == null || newPassword.isBlank()) return true; // 변경 안 함
        return currentPassword != null && !currentPassword.isBlank();  // 변경 시 현재 비번 필수
    }
}