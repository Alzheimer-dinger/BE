package opensource.alzheimerdinger.core.domain.reminder.application.usecase;

import opensource.alzheimerdinger.core.domain.reminder.application.dto.request.ReminderSettingRequest;
import opensource.alzheimerdinger.core.domain.reminder.domain.service.ReminderService;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderCommandUseCaseTest {

    @Mock
    ReminderService reminderService;

    @Mock
    UserService userService;

    @InjectMocks
    ReminderCommandUseCase useCase;

    @Test
    void register_callsServicesInOrder() {
        String userId = "user-123";
        ReminderSettingRequest request = mock(ReminderSettingRequest.class);
        User user = mock(User.class);

        when(userService.findUser(userId)).thenReturn(user);

        useCase.register(userId, request);

        verify(userService).findUser(userId);
        verify(reminderService).upsert(user, request);
        verifyNoMoreInteractions(userService, reminderService);
    }
}
