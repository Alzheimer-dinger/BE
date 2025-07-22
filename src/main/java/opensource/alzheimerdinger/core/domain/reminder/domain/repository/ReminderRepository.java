package opensource.alzheimerdinger.core.domain.reminder.domain.repository;

import opensource.alzheimerdinger.core.domain.reminder.domain.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, String> {
}
