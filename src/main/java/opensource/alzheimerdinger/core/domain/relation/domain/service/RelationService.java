package opensource.alzheimerdinger.core.domain.relation.domain.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.relation.domain.repository.RelationRepository;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.stereotype.Service;

import java.util.List;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;

    public Relation save(User patient, User guardian, RelationStatus status, Role initiator) {
        Relation relation = Relation.builder()
                .patient(patient)
                .guardian(guardian)
                .relationStatus(status)
                .initiator(initiator)
                .build();

        return relationRepository.save(relation);
    }


    public List<RelationResponse> findRelations(String userId) {
        return relationRepository.findRelation(userId);
    }

    public Relation findRelation(String relationId) {
        return relationRepository.findById(relationId)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));
    }

    public boolean existsByGuardianAndPatient(User guardian, User patient) {
        return relationRepository.existsByUsers(guardian, patient);
    }
}
