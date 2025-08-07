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

        String counterFcmToken = fcmTokenService.findByUser(counter);
        String myFcmToken = fcmTokenService.findByUser(user);

        if (RelationStatus.ACCEPTED.equals(status)) {
            notificationService.sendNotification(counterFcmToken, myName + "님이 보호 관계 요청을 수락했어요.", "", counter.getUserId());
            notificationService.sendNotification(myFcmToken, counter.getName() + "님과 이제 보호 관계가 맺어졌어요 ", "", user.getUserId());
        }
        else if (RelationStatus.REJECTED.equals(status))
            notificationService.sendNotification(counterFcmToken, myName + "님이 보호 관계 요청을 거절했어요.", "", counter.getUserId());
    }

    public void sendRequestNotification(User patient, User guardian) {
        String fcmToken = fcmTokenService.findByUser(patient);
        notificationService.sendNotification(fcmToken, guardian.getName() + "님이 보호 관계를 요청했어요.", "", patient.getUserId());
    }

    public void sendResendRequestNotification(User patient, Relation relation) {
        User guardian = relation.getCounter(patient);
        String fcmToken = fcmTokenService.findByUser(guardian);
        notificationService.sendNotification(fcmToken, patient.getName() + "님이 보호 관계를 재요청했어요.", "", guardian.getUserId());
    }

    public void sendDisconnectNotification(User user, Relation relation) {
        User counter = relation.getCounter(user);
        String fcmToken = fcmTokenService.findByUser(counter);
        notificationService.sendNotification(fcmToken, user.getName() + "님이 보호 관계를 해제하였어요.", "", counter.getUserId());
    }
}
