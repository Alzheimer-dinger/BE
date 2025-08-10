package opensource.alzheimerdinger.core.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.UpdateProfileRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.repository.UserRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;
import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ProfileResponse updateProfile(String userId, UpdateProfileRequest req) {
        log.debug("[UserProfile] updateProfile start: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[UserProfile] user not found: userId={}", userId);
                    return new RestApiException(_NOT_FOUND);
                });

        String encodedNewPassword = null;

        // 새 비밀번호가 들어온 경우에만 현재 비번 검증 + 해싱
        if (req.newPassword() != null && !req.newPassword().isBlank()) {
            if (req.currentPassword() == null || req.currentPassword().isBlank()
                    || !passwordEncoder.matches(req.currentPassword(), user.getPassword())) {
                log.warn("[UserProfile] password change failed: userId={} (current mismatch or empty)", userId);
                throw new RestApiException(_UNAUTHORIZED);
            }
            encodedNewPassword = passwordEncoder.encode(req.newPassword());
        }

        // 엔티티에 값 반영
        user.updateProfile(req.name(), req.gender(), encodedNewPassword);

        // @Transactional + JPA dirty checking으로 자동 반영
        log.info("[UserProfile] updateProfile success: userId={}", user.getUserId());
        return ProfileResponse.create(user);
    }
}