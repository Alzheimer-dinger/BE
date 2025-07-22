package opensource.alzheimerdinger.core.domain.relation.application.usecase;

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

    public List<RelationResponse> findRelations(String userId) {
        return relationService.findRelations(userId);
    }

    public void reply(String userId, String relationId, RelationStatus status) {
        Relation relation = relationService.findRelation(relationId);

        if(!relation.getRelationStatus().equals(RelationStatus.REQUESTED))
            throw new RestApiException(_NOT_FOUND);

        if (!relation.isReceiver(userId))
            throw new RestApiException(_UNAUTHORIZED);

        relation.updateStatus(status);
    }

    public void send(String userId, RelationConnectRequest request) {
        User guardian = userService.findUser(userId);
        User patient = userService.findUser(request.to());

        if(relationService.existsByGuardianAndPatient(guardian, patient))
            throw new RestApiException(_EXIST_ENTITY);

        relationService.save(patient, guardian, RelationStatus.REQUESTED, Role.GUARDIAN);
    }

    public void resend(String userId, RelationReconnectRequest request) {
        Relation relation = relationService.findRelation(request.relationId());

        if(relation.getRelationStatus() != RelationStatus.DISCONNECTED)
            throw new RestApiException(_NOT_FOUND);

        if(relation.isMember(userId))
            throw new RestApiException(_UNAUTHORIZED);

        relation.resend(userId);
    }

    public void disconnect(String userId, String relationId) {
        Relation relation = relationService.findRelation(relationId);

        if (!relation.isMember(userId))
            throw new RestApiException(_NOT_FOUND);

        relation.updateStatus(RelationStatus.DISCONNECTED); // CANCELED + DISCONNECTED
    }
}
