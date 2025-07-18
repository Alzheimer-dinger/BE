package opensource.alzheimerdinger.core.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.Relation;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.repository.RelationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;
    private final UserService userService;

    public Relation save(User patient, User guardian) {
        Relation relation = new Relation(patient, guardian);
        return relationRepository.save(relation);
    }


    public List<Relation> findRelations(String userId) {
        User me = userService.findUser(userId);
        return relationRepository.findRelation(me);
    }
}
