package opensource.alzheimerdinger.core.domain.reminder.application.dto.request;

import jakarta.validation.constraints.NotNull;
import opensource.alzheimerdinger.core.domain.reminder.domain.entity.ReminderStatus;

import java.time.LocalTime;

public record ReminderSettingRequest(
        LocalTime fireTime,
        @NotNull ReminderStatus status
) {}
