package opensource.alzheimerdinger.core.domain.reminder.application.usecase;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import opensource.alzheimerdinger.core.domain.reminder.application.dto.request.ReminderSettingRequest;
import opensource.alzheimerdinger.core.domain.reminder.domain.entity.ReminderStatus;
import opensource.alzheimerdinger.core.domain.reminder.domain.service.ReminderService;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReminderCommandUseCaseTest {

    @Mock
    private ReminderService reminderService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReminderCommandUseCase useCase;

    private final String userId = "user-123";
    private User user;
    private ReminderSettingRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        request = new ReminderSettingRequest(LocalTime.now(), ReminderStatus.ACTIVE);
    }

    @Test
    void register_whenUserExists_callsUpsert() {
        // given
        when(userService.findUser(userId)).thenReturn(user);

        // when
        useCase.register(userId, request);

        // then
        verify(userService, times(1)).findUser(userId);
        verify(reminderService, times(1)).upsert(eq(user), eq(request));
    }

    @Test
    void register_whenUserNotFound_propagatesException() {
        // given
        when(userService.findUser(userId)).thenThrow(new RuntimeException("user not found"));

        // when / then
        assertThatThrownBy(() -> useCase.register(userId, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("user not found");

        verify(userService, times(1)).findUser(userId);
        verifyNoInteractions(reminderService);
    }
}
