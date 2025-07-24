package opensource.alzheimerdinger.core.domain.relation.application.usecase;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RelationManagementUseCase {

    private final RelationService relationService;
    private final UserService userService;
    private final MeterRegistry registry;

    public List<RelationResponse> findRelations(String userId) {
        registry.counter("domain_relation_find_requests").increment(); // 호출 횟수
        return registry.timer("domain_relation_find_duration", "domain", "relation") // 실행 시간
                .record(() -> relationService.findRelations(userId));
    }

    public void reply(String userId, String relationId, RelationStatus status) {
        registry.counter("domain_relation_reply_requests").increment();
        registry.timer("domain_relation_reply_duration", "domain", "relation")
                .record(() -> {
                    Relation r = relationService.findRelation(relationId);
                    if (!r.getRelationStatus().equals(RelationStatus.REQUESTED))
                        throw new RestApiException(_NOT_FOUND);
                    if (!r.isReceiver(userId))
                        throw new RestApiException(_UNAUTHORIZED);
                    r.updateStatus(status);
                });
    }

    public void send(String userId, RelationConnectRequest req) {
        registry.counter("domain_relation_send_requests").increment();
        registry.timer("domain_relation_send_duration", "domain", "relation")
                .record(() -> {
                    User guardian = userService.findUser(userId);
                    User patient  = userService.findUser(req.to());
                    relationService.save(patient, guardian, RelationStatus.REQUESTED, Role.GUARDIAN);
                });
    }

    public void resend(String userId, RelationReconnectRequest req) {
        registry.counter("domain_relation_resend_requests").increment();
        registry.timer("domain_relation_resend_duration", "domain", "relation")
                .record(() -> {
                    Relation r = relationService.findRelation(req.relationId());
                    if (r.getRelationStatus() != RelationStatus.DISCONNECTED)
                        throw new RestApiException(_NOT_FOUND);
                    if (r.isMember(userId))
                        throw new RestApiException(_UNAUTHORIZED);
                    r.resend(userId);
                });
    }

    public void disconnect(String userId, String relationId) {
        registry.counter("domain_relation_disconnect_requests").increment();
        registry.timer("domain_relation_disconnect_duration", "domain", "relation")
                .record(() -> {
                    Relation r = relationService.findRelation(relationId);
                    if (!r.isMember(userId))
                        throw new RestApiException(_NOT_FOUND);
                    r.updateStatus(RelationStatus.DISCONNECTED);
                });
    }
}
