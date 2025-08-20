package opensource.alzheimerdinger.core.domain.user.application.usecase;


import opensource.alzheimerdinger.core.domain.user.application.dto.request.UpdateProfileRequest;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Gender;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._UNAUTHORIZED;

@ExtendWith(MockitoExtension.class)
class UpdateProfileUseCaseTest {

    @Mock
    private UserService userService;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UpdateProfileUseCase useCase;

    private User existing;

    @BeforeEach
    void setUp() {
        existing = User.builder()
                .userId("U1")
                .name("Old Name")
                .email("old@example.com")
                .password("$2a$10$oldhash") // 가짜 해시
                .role(Role.PATIENT)
                .gender(Gender.MALE)
                .build();
    }

    @Test
    @DisplayName("이름/성별만 변경: 비번은 그대로 유지")
    void update_profile_without_password_change() {
        // given
        UpdateProfileRequest req = new UpdateProfileRequest(
                "New Name",
                Gender.FEMALE,
                null,     // currentPassword
                null      // newPassword
        );
        when(userService.findUser("U1")).thenReturn(existing);

        // when
        ProfileResponse res = useCase.update("U1", req);

        // then
        assertThat(res.name()).isEqualTo("New Name");
        assertThat(res.gender()).isEqualTo(Gender.FEMALE);
        assertThat(existing.getPassword()).isEqualTo("$2a$10$oldhash"); // 그대로
        verify(passwordEncoder, never()).encode(anyString());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("비번 변경: 현재 비번 일치 → 새 비번 해싱 저장")
    void update_profile_with_password_change_success() {
        // given
        UpdateProfileRequest req = new UpdateProfileRequest(
                "New Name",
                Gender.FEMALE,
                "current-plain", // 현재 비번
                "new-plain"      // 새 비번
        );
        when(userService.findUser("U1")).thenReturn(existing);
        when(passwordEncoder.matches("current-plain", "$2a$10$oldhash")).thenReturn(true);
        when(passwordEncoder.encode("new-plain")).thenReturn("$2a$10$newhash");

        // when
        ProfileResponse res = useCase.update("U1", req);

        // then
        assertThat(res.name()).isEqualTo("New Name");
        assertThat(res.gender()).isEqualTo(Gender.FEMALE);
        assertThat(existing.getPassword()).isEqualTo("$2a$10$newhash");
        verify(passwordEncoder).matches("current-plain", "$2a$10$oldhash");
        verify(passwordEncoder).encode("new-plain");
    }

    @Test
    @DisplayName("비번 변경: 현재 비번 불일치 → UNAUTHORIZED 예외")
    void update_profile_with_password_change_mismatch() {
        // given
        UpdateProfileRequest req = new UpdateProfileRequest(
                "New Name",
                Gender.FEMALE,
                "wrong-current",
                "new-plain"
        );
        when(userService.findUser("U1")).thenReturn(existing);
        when(passwordEncoder.matches("wrong-current", "$2a$10$oldhash")).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> useCase.update("U1", req))
                .isInstanceOf(RestApiException.class)
                .satisfies(ex -> {
                    RestApiException e = (RestApiException) ex;
                    // 401 (UNAUTHORIZED)
                    assertThat(e.getErrorCode().getHttpStatus().value()).isEqualTo(401);
                    // 프로젝트 통일 코드라면 (예: COMMON401)
                    assertThat(e.getErrorCode().getCode()).isEqualTo("COMMON401");
                });

        // 비번은 그대로
        assertThat(existing.getPassword()).isEqualTo("$2a$10$oldhash");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("UserService에서 _NOT_FOUND 던질 때 그대로 전파")
    void user_not_found_bubbles_up() {
        // given
        UpdateProfileRequest req = new UpdateProfileRequest(
                "New Name",
                Gender.FEMALE,
                null,
                null
        );
        when(userService.findUser("U2")).thenThrow(new RestApiException(
                opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND
        ));

        // when / then
        assertThatThrownBy(() -> useCase.update("U2", req))
                .isInstanceOf(RestApiException.class);
    }
}