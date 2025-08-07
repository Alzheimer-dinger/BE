package opensource.alzheimerdinger.core.domain.reminder.application.usecase;

import opensource.alzheimerdinger.core.domain.reminder.application.dto.response.ReminderResponse;
import opensource.alzheimerdinger.core.domain.reminder.domain.entity.Reminder;
import opensource.alzheimerdinger.core.domain.reminder.domain.service.ReminderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderQueryUseCaseTest {

    @Mock
    ReminderService reminderService;

    @InjectMocks
    ReminderQueryUseCase useCase;

    @Test
    void find_returnsFireTime() {
        String userId = "user-123";
        LocalTime fireTime = LocalTime.of(7, 4);

        Reminder reminder = mock(Reminder.class);
        when(reminder.getFireTime()).thenReturn(fireTime);
        when(reminderService.findReminder(userId)).thenReturn(reminder);

        ReminderResponse res = useCase.find(userId);

        assertThat(res.fireTime()).isEqualTo(fireTime);
        verify(reminderService).findReminder(userId);
        verifyNoMoreInteractions(reminderService);
    }
}
