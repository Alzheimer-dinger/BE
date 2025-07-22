package opensource.alzheimerdinger.core.domain.reminder.application.dto.request;

import opensource.alzheimerdinger.core.domain.reminder.domain.entity.ReminderStatus;

import java.time.LocalTime;

public record ReminderSettingRequest(
        LocalTime fireTime,
        ReminderStatus status
) {}
