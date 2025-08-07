package opensource.alzheimerdinger.core.domain.reminder.application.dto.response;

import opensource.alzheimerdinger.core.domain.reminder.domain.entity.ReminderStatus;

import java.time.LocalTime;

public record ReminderResponse (
        LocalTime fireTime,
        ReminderStatus status
) {}
