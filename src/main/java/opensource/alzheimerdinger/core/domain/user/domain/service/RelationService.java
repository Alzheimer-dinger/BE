package opensource.alzheimerdinger.core.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.repository.RelationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;

    public Relation save(User patient, User guardian) {
        Relation relation = new Relation(patient, guardian);
        return relationRepository.save(relation);
    }
}
