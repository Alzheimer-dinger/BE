package opensource.alzheimerdinger.core.domain.user.application.usecase;

import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileUseCaseTest {

    @Mock  UserService userService;
    @InjectMocks  UserProfileUseCase sut;

    @Test
    void findProfile_success() {
        // given
        String userId = "user-1";
        ProfileResponse expected = mock(ProfileResponse.class);
        when(userService.findProfile(userId)).thenReturn(expected);

        // when
        ProfileResponse actual = sut.findProfile(userId);

        // then
        assertThat(actual).isSameAs(expected);
        verify(userService).findProfile(userId);
        verifyNoMoreInteractions(userService);
    }
}
