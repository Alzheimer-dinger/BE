package opensource.alzheimerdinger.core.domain.reminder.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.global.common.BaseEntity;

import java.time.LocalTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Reminder extends BaseEntity {

    @Id @Tsid
    private String userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalTime fireTime;

    private ReminderStatus status;

    public void update(LocalTime fireTime, ReminderStatus status) {
        this.fireTime = fireTime;
        this.status = status;
    }

    public void updateStatus(ReminderStatus status) {
        this.status = status;
    }

    @PrePersist
    public void init() {
        status = ReminderStatus.ACTIVE;
    }
}
