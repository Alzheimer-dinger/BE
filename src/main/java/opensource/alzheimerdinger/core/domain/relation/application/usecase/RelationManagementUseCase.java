package opensource.alzheimerdinger.core.domain.relation.application.usecase;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.notification.usecase.NotificationUseCase;
import opensource.alzheimerdinger.core.domain.relation.application.dto.request.RelationConnectRequest;
import opensource.alzheimerdinger.core.domain.relation.application.dto.request.RelationReconnectRequest;
import opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
import opensource.alzheimerdinger.core.domain.relation.domain.service.RelationService;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import opensource.alzheimerdinger.core.global.metric.UseCaseMetric;
import org.springframework.stereotype.Service;

import java.util.List;

import static opensource.alzheimerdinger.core.domain.user.domain.entity.Role.GUARDIAN;
import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;
import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._UNAUTHORIZED;

@Service
@Transactional
@RequiredArgsConstructor
public class RelationManagementUseCase {

    private final RelationService relationService;
    private final UserService userService;
    private final NotificationUseCase notificationUseCase;

    @UseCaseMetric(domain = "relation", value = "find", type = "query")
    public List<RelationResponse> findRelations(String userId) {
        return relationService.findRelations(userId);
    }

    @UseCaseMetric(domain = "relation", value = "reply", type = "command")
    public void reply(String userId, String relationId, RelationStatus status) {
        Relation relation = relationService.findRelation(relationId);
        User user = userService.findUser(userId);

        if (!RelationStatus.REQUESTED.equals(relation.getRelationStatus()))
            throw new RestApiException(_NOT_FOUND);
        if (!relation.isReceiver(user))
            throw new RestApiException(_UNAUTHORIZED);

        relation.updateStatus(status);

        if (RelationStatus.ACCEPTED.equals(status)) {
            user.updateRole(GUARDIAN);
        }

        notificationUseCase.sendReplyNotification(user, relation, status);
    }


    @UseCaseMetric(domain = "relation", value = "send", type = "command")
    public void send(String userId, RelationConnectRequest req) {
        User guardian = userService.findUser(userId);
        User patient  = userService.findPatient(req.patientCode());

        relationService.save(patient, guardian, RelationStatus.REQUESTED, GUARDIAN);
        notificationUseCase.sendRequestNotification(patient, guardian);
    }

    @UseCaseMetric(domain = "relation", value = "resend", type = "command")
    public void resend(String userId, RelationReconnectRequest req) {
        Relation relation = relationService.findRelation(req.relationId());
        User user = userService.findUser(userId);

        if (relation.getRelationStatus() != RelationStatus.DISCONNECTED)
            throw new RestApiException(_NOT_FOUND);
        if (!relation.isMember(user))
            throw new RestApiException(_UNAUTHORIZED);

        relation.resend(userId);
        notificationUseCase.sendResendRequestNotification(user, relation);
    }

    @UseCaseMetric(domain = "relation", value = "disconnect", type = "command")
    public void disconnect(String userId, String relationId) {
        Relation relation = relationService.findRelation(relationId);
        User user = userService.findUser(userId);

        if (!relation.isMember(user))
            throw new RestApiException(_NOT_FOUND);

        relation.updateStatus(RelationStatus.DISCONNECTED);
        notificationUseCase.sendDisconnectNotification(user, relation);
    }
}
