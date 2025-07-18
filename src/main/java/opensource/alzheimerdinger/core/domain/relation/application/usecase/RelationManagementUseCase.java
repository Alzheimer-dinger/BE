package opensource.alzheimerdinger.core.domain.relation.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse;
import opensource.alzheimerdinger.core.domain.user.domain.service.RelationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationManagementUseCase {

    private final RelationService relationService;

    public RelationResponse findRelations(String userId) {
        List<RelationResponse> relations = relationService.findRelations(userId).stream()
                .map(RelationResponse::create)
                .toList();
    }
}
