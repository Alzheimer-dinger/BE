package opensource.alzheimerdinger.core.domain.user.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.domain.service.ImageService;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.UpdateProfileRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.metric.UseCaseMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;
import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._UNAUTHORIZED;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateProfileUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateProfileUseCase.class);

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    @UseCaseMetric(domain = "user-profile", value = "update-profile", type = "command")
    public ProfileResponse update(String userId, UpdateProfileRequest req) {
        log.debug("[UpdateProfile] start: userId={}", userId);

        User user = userService.findUser(userId);
        if (user == null) throw new RestApiException(_NOT_FOUND);

        String encodedNewPassword = null;

        // 새 비번 요청이 있는 경우 → 현재 비번 일치 여부 검사 (불일치 시 권한 오류)
        if (req.newPassword() != null && !req.newPassword().isBlank()) {
            boolean matches = passwordEncoder.matches(req.currentPassword(), user.getPassword());
            if (!matches) {
                log.warn("[UpdateProfile] password mismatch: userId={}", userId);
                throw new RestApiException(_UNAUTHORIZED);
            }
            encodedNewPassword = passwordEncoder.encode(req.newPassword());
        }

        user.updateProfile(req.name(), req.gender(), encodedNewPassword);

        String profileImageUrl = imageService.getProfileImageUrl(user);

        log.info("[UpdateProfile] success: userId={}", user.getUserId());
        return ProfileResponse.create(user, profileImageUrl);
    }
}