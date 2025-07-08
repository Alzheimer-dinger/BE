package opensource.alzheimerdinger.core.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.request.SignUpRequest;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean isAlreadyRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    public void signUp(SignUpRequest request) {
        userRepository.save(
                User.builder()
                        .email(request.email())
                        .password()
                        .role(request.role())
        );
    }
}
