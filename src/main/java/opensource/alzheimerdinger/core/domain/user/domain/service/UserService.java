package opensource.alzheimerdinger.core.domain.user.domain.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpToGuardianRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.request.SignUpToPatientRequest;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.repository.UserRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));
    }

    public boolean isAlreadyRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(SignUpToPatientRequest request, String code) {
        return userRepository.save(
                User.builder()
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .role(Role.PATIENT)
                        .patientCode(code)
                        .build()
        );
    }

    public User save(SignUpToGuardianRequest request) {
        return userRepository.save(
                User.builder()
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .role(Role.GUARDIAN)
                        .build()
        );
    }

    public User findPatient(String code) {
        return userRepository.findByPatientCode(code)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));
    }

    public User findUser(String userId) {
        return  userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));
    }
}
