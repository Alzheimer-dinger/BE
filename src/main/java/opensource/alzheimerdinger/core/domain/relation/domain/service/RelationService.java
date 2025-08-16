package opensource.alzheimerdinger.core.domain.relation.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Role;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.relation.domain.repository.RelationRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RelationService {
    private static final Logger log = LoggerFactory.getLogger(RelationService.class);
    private final RelationRepository relationRepository;

    public Relation upsert(User patient, User guardian, RelationStatus status, Role initiator) {
        log.debug("[RelationService] creating relation: patientId={} guardianId={} initiator={}",
                patient.getUserId(), guardian.getUserId(), initiator);

        Relation relation = relationRepository.findByPatientAndGuardian(patient, guardian)
                .map(r -> {
                    r.update(status, initiator);
                    return r;
                })
                .orElseGet(() -> Relation.builder()
                        .patient(patient)
                        .guardian(guardian)
                        .relationStatus(status)
                        .initiator(initiator)
                        .build());


        Relation saved = relationRepository.save(relation);
        log.info("[RelationService] relation created: relationId={}", saved.getRelationId());
        return saved;
    }


    public List<RelationResponse> findRelations(String userId) {
        // 보통 조회 쿼리는 로깅 생략, 필요시 debug로만 처리
        log.debug("[RelationService] finding relations for userId={}", userId);
        return relationRepository.findRelation(userId);
    }

    public Relation findRelation(String relationId) {
        log.debug("[RelationService] finding relation by id={}", relationId);
        Relation r = relationRepository.findById(relationId)
                .orElseThrow(() -> {
                    log.warn("[RelationService] relation not found: {}", relationId);
                    return new RestApiException(_NOT_FOUND);
                });
        log.debug("[RelationService] found relation status={}", r.getRelationStatus());
        return r;
    }

    public boolean existsByGuardianAndPatient(User guardian, User patient) {
        boolean exists = relationRepository.existsByUsers(guardian, patient);
        log.debug("[RelationService] existsByGuardianAndPatient guardianId={} patientId={} => {}",
                guardian.getUserId(), patient.getUserId(), exists);
        return exists;
    }
}
