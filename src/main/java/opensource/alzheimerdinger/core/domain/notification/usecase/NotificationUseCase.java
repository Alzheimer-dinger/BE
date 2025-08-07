package opensource.alzheimerdinger.core.domain.notification.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.notification.service.FcmTokenService;
import opensource.alzheimerdinger.core.domain.notification.service.NotificationService;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationUseCase {

    private final FcmTokenService fcmTokenService;
    private final NotificationService notificationService;

    public void sendReplyNotification(User user, Relation relation, RelationStatus status) {
        User counter = relation.getCounter(user);
        String myName = user.getName();

        String fcmToken = fcmTokenService.findByUser(user);

        if (RelationStatus.ACCEPTED.equals(status))
            notificationService.sendNotification(fcmToken, myName + "님이 보호 관계 요청을 수락했습니다.", myName + "님이 보호 관계 요청을 수락했습니다.", counter.getUserId());
        else if (RelationStatus.REJECTED.equals(status))
            notificationService.sendNotification(fcmToken, myName + "님이 보호 관계 요청을 거절했습니다.", myName + "님이 보호 관계 요청을 거절했습니다.", counter.getUserId());
    }

    public void sendRequestNotification(User patient, User guardian) {
        String fcmToken = fcmTokenService.findByUser(patient);
        notificationService.sendNotification(fcmToken, guardian.getName() + "님이 보호 관계를 요청했습니다.", "", patient.getUserId());
    }

    public void sendResendRequestNotification(User patient, Relation relation) {
        User guardian = relation.getCounter(patient);
        String fcmToken = fcmTokenService.findByUser(guardian);
        notificationService.sendNotification(fcmToken, patient.getName() + "님이 보호 관계를 재요청했습니다.", "", guardian.getUserId());
    }

    public void sendDisconnectNotification(User user, Relation relation) {
        User counter = relation.getCounter(user);
        String fcmToken = fcmTokenService.findByUser(counter);
        notificationService.sendNotification(fcmToken, user.getName() + "님이 보호 관계를 해제하였습니다.", "", counter.getUserId());
    }
}
