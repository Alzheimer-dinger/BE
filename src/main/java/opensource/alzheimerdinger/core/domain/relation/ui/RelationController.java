package opensource.alzheimerdinger.core.domain.relation.ui;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse;
import opensource.alzheimerdinger.core.domain.relation.application.usecase.RelationManagementUseCase;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/relations")
public class RelationController {

    private final RelationManagementUseCase relationManagementUseCase;

    @GetMapping
    public BaseResponse<RelationResponse> getRelations(@CurrentUser String userId) {
        return BaseResponse.onSuccess(relationManagementUseCase.findRelations(userId));
    }
}
