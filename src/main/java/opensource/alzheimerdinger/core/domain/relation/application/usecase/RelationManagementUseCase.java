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
    private final MeterRegistry registry;
    private final NotificationUseCase notificationUseCase;

    public List<RelationResponse> findRelations(String userId) {
        registry.counter("domain_relation_find_requests").increment(); // 호출 횟수
        return registry.timer("domain_relation_find_duration", "domain", "relation") // 실행 시간
                .record(() -> relationService.findRelations(userId));
    }

    public void reply(String userId, String relationId, RelationStatus status) {
        registry.counter("domain_relation_reply_requests").increment();
        registry.timer("domain_relation_reply_duration", "domain", "relation")
                .record(() -> {
                    Relation relation = relationService.findRelation(relationId);
                    User user = userService.findUser(userId);

                    if (!relation.getRelationStatus().equals(RelationStatus.REQUESTED))
                        throw new RestApiException(_NOT_FOUND);
                    if (!relation.isReceiver(user))
                        throw new RestApiException(_UNAUTHORIZED);

                    relation.updateStatus(status);

                    if(RelationStatus.ACCEPTED.equals(status))
                        user.updateRole(GUARDIAN);

                    notificationUseCase.sendReplyNotification(user, relation, status);
                });
    }

    public void send(String userId, RelationConnectRequest req) {
        registry.counter("domain_relation_send_requests").increment();
        registry.timer("domain_relation_send_duration", "domain", "relation")
                .record(() -> {
                    User guardian = userService.findUser(userId);
                    User patient  = userService.findUser(req.to());

                    relationService.save(patient, guardian, RelationStatus.REQUESTED, GUARDIAN);
                    notificationUseCase.sendRequestNotification(patient, guardian);
                });
    }

    public void resend(String userId, RelationReconnectRequest req) {
        registry.counter("domain_relation_resend_requests").increment();
        registry.timer("domain_relation_resend_duration", "domain", "relation")
                .record(() -> {
                    Relation relation = relationService.findRelation(req.relationId());
                    User user = userService.findUser(userId);

                    if (relation.getRelationStatus() != RelationStatus.DISCONNECTED)
                        throw new RestApiException(_NOT_FOUND);
                    if (relation.isMember(user))
                        throw new RestApiException(_UNAUTHORIZED);

                    relation.resend(userId);
                    notificationUseCase.sendResendRequestNotification(user, relation);
                });
    }

    public void disconnect(String userId, String relationId) {
        registry.counter("domain_relation_disconnect_requests").increment();
        registry.timer("domain_relation_disconnect_duration", "domain", "relation")
                .record(() -> {
                    Relation relation = relationService.findRelation(relationId);
                    User user = userService.findUser(userId);

                    if (!relation.isMember(user))
                        throw new RestApiException(_NOT_FOUND);

                    relation.updateStatus(RelationStatus.DISCONNECTED);
                    notificationUseCase.sendDisconnectNotification(user, relation);
                });
    }
}
