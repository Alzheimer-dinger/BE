package opensource.alzheimerdinger.core.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.domain.service.ImageService;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpToGuardianRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.repository.UserRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    public User findByEmail(String email) {
        log.debug("[UserService] findByEmail: email={}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[UserService] user not found by email={}", email);
                    return new RestApiException(_NOT_FOUND);
                });
    }

    public boolean isAlreadyRegistered(String email) {
        log.debug("[UserService] isAlreadyRegistered: email={}", email);
        boolean exists = userRepository.existsByEmail(email);
        log.info("[UserService] isAlreadyRegistered result: email={} exists={}", email, exists);
        return exists;
    }

    public User save(SignUpRequest request, String code) {
        log.info("[UserService] save new user: email={} code={} role={}",
                request.email(), code,
                request.patientCode() == null ? Role.PATIENT : Role.GUARDIAN);
        User user = userRepository.save(
                User.builder()
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .role(request.patientCode() == null ? Role.PATIENT : Role.GUARDIAN)
                        .patientCode(code)
                        .gender(request.gender())
                        .name(request.name())
                        .build()
        );
        log.debug("[UserService] saved userId={}", user.getUserId());
        return user;
    }

    public User findPatient(String code) {
        log.debug("[UserService] findPatient by code={}", code);
        return userRepository.findByPatientCode(code)
                .orElseThrow(() -> {
                    log.warn("[UserService] patient not found by code={}", code);
                    return new RestApiException(_NOT_FOUND);
                });
    }

    public User findUser(String userId) {
        log.debug("[UserService] findUser by userId={}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[UserService] user not found by userId={}", userId);
                    return new RestApiException(_NOT_FOUND);
                });
    }


    public ProfileResponse findProfile(String userId) {
        log.debug("[UserService] findProfile for userId={}", userId);
        ProfileResponse profile = userRepository.findById(userId)
                .map(user -> {
                    String profileImageUrl = imageService.getProfileImageUrl(user);
                    return ProfileResponse.create(user, profileImageUrl);
                })
                .orElseThrow(() -> {
                    log.warn("[UserService] profile not found for userId={}", userId);
                    return new RestApiException(_NOT_FOUND);
                });
        log.info("[UserService] returning profile for userId={}", userId);
        return profile;
    }
}
