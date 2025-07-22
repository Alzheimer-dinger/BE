package opensource.alzheimerdinger.core.domain.reminder.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.reminder.application.dto.request.ReminderSettingRequest;
import opensource.alzheimerdinger.core.domain.reminder.domain.service.ReminderService;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReminderCommandUseCase {

    private final ReminderService reminderService;
    private final UserService userService;

    public void register(String userId, ReminderSettingRequest request) {
        User user = userService.findUser(userId);
        reminderService.upsert(user, request);
    }
}
