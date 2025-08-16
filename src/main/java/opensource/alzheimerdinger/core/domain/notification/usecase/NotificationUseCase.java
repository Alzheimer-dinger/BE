package opensource.alzheimerdinger.core.domain.notification.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.notification.service.FcmTokenService;
import opensource.alzheimerdinger.core.domain.notification.service.NotificationService;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.global.metric.UseCaseMetric;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationUseCase {

    private final FcmTokenService fcmTokenService;
    private final NotificationService notificationService;

    @UseCaseMetric(domain = "notification", value = "send-reply", type = "command")
    public void sendReplyNotification(User user, Relation relation, RelationStatus status) {
        User counter = relation.getCounter(user);
        String myName = user.getName();

        Optional<String> counterFcmToken = fcmTokenService.findByUser(counter);
        Optional<String> myFcmToken = fcmTokenService.findByUser(user);

        if(counterFcmToken.isEmpty() || myFcmToken.isEmpty())
            return;

        if (RelationStatus.ACCEPTED.equals(status)) {
            notificationService.sendNotification(counterFcmToken.get(), myName + "님이 보호 관계 요청을 수락했어요.", "", counter.getUserId());
            notificationService.sendNotification(myFcmToken.get(), counter.getName() + "님과 이제 보호 관계가 맺어졌어요 ", "", user.getUserId());
        }
        else if (RelationStatus.REJECTED.equals(status))
            notificationService.sendNotification(counterFcmToken.get(), myName + "님이 보호 관계 요청을 거절했어요.", "", counter.getUserId());
    }

    @UseCaseMetric(domain = "notification", value = "send-request", type = "command")
    public void sendRequestNotification(User patient, User guardian) {
        Optional<String> fcmToken = fcmTokenService.findByUser(patient);

        if (fcmToken.isEmpty())
            return;

        notificationService.sendNotification(fcmToken.get(), guardian.getName() + "님이 보호 관계를 요청했어요.", "", patient.getUserId());
    }

    @UseCaseMetric(domain = "notification", value = "send-resend-request", type = "command")
    public void sendResendRequestNotification(User patient, Relation relation) {
        User guardian = relation.getCounter(patient);
        Optional<String> fcmToken = fcmTokenService.findByUser(guardian);

        if (fcmToken.isEmpty())
            return;

        notificationService.sendNotification(fcmToken.get(), patient.getName() + "님이 보호 관계를 재요청했어요.", "", guardian.getUserId());
    }

    @UseCaseMetric(domain = "notification", value = "send-disconnect", type = "command")
    public void sendDisconnectNotification(User user, Relation relation) {
        User counter = relation.getCounter(user);
        Optional<String> fcmToken = fcmTokenService.findByUser(counter);

        if (fcmToken.isEmpty())
            return;

        notificationService.sendNotification(fcmToken.get(), user.getName() + "님이 보호 관계를 해제하였어요.", "", counter.getUserId());
    }
}
