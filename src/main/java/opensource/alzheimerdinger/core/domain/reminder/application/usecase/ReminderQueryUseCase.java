package opensource.alzheimerdinger.core.domain.reminder.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.reminder.application.dto.response.ReminderResponse;
import opensource.alzheimerdinger.core.domain.reminder.domain.entity.Reminder;
import opensource.alzheimerdinger.core.domain.reminder.domain.service.ReminderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReminderQueryUseCase {

    private final ReminderService reminderService;

    public ReminderResponse find(String userId) {
        Reminder reminder = reminderService.findReminder(userId);
        return new ReminderResponse(reminder.getFireTime(), reminder.getStatus());
    }
}
