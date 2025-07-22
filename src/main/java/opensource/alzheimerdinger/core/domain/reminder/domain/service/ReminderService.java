package opensource.alzheimerdinger.core.domain.reminder.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.reminder.application.dto.request.ReminderSettingRequest;
import opensource.alzheimerdinger.core.domain.reminder.domain.entity.Reminder;
import opensource.alzheimerdinger.core.domain.reminder.domain.repository.ReminderRepository;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.stereotype.Service;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public void upsert(User user, ReminderSettingRequest request) {
        Reminder reminder = reminderRepository.findById(user.getUserId())
                .orElseGet(() -> Reminder.builder()
                        .user(user)
                        .build());

        reminder.update(request.fireTime(), request.status());
        reminderRepository.save(reminder);
    }

    public Reminder findReminder(String userId) {
        return reminderRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));
    }
}
