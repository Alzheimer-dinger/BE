package opensource.alzheimerdinger.core.domain.relation.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.relation.application.dto.request.RelationConnectRequest;
import opensource.alzheimerdinger.core.domain.relation.application.dto.request.RelationReconnectRequest;
import opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.relation.domain.service.RelationService;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.stereotype.Service;

import java.util.List;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class RelationManagementUseCase {

    private final RelationService relationService;
    private final UserService userService;

    public List<RelationResponse> findRelations(String userId) {
        return relationService.findRelations(userId);
    }

    public void reply(String userId, String relationId) {

    }

    public void send(String userId, RelationConnectRequest request) {
        User guardian = userService.findUser(userId);
        User patient = userService.findUser(request.to());
        relationService.save(patient, guardian, RelationStatus.REQUESTED, Role.GUARDIAN);
    }

    public void resend(String userId, RelationReconnectRequest request) {
        if(!relationService.validate(request.relationId()))
            throw new RestApiException(_NOT_FOUND);

        User patient = userService.findUser(userId);
        User guardian = userService.findUser(request.to());
        relationService.save(guardian, patient, RelationStatus.REQUESTED, Role.PATIENT);
    }
}
