package opensource.alzheimerdinger.core.domain.reminder.application.dto.response;

import java.time.LocalTime;

public record ReminderResponse (
        LocalTime fireTime
) {}
