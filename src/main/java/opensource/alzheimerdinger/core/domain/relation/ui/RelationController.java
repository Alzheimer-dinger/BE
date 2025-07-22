package opensource.alzheimerdinger.core.domain.relation.ui;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.relation.application.dto.request.RelationConnectRequest;
import opensource.alzheimerdinger.core.domain.relation.application.dto.request.RelationReconnectRequest;
import opensource.alzheimerdinger.core.domain.relation.application.dto.response.RelationResponse;
import opensource.alzheimerdinger.core.domain.relation.application.usecase.RelationManagementUseCase;
import opensource.alzheimerdinger.core.domain.relation.domain.entity.RelationStatus;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/relations")
public class RelationController {

    private final RelationManagementUseCase relationManagementUseCase;

    @GetMapping
    public BaseResponse<List<RelationResponse>> getRelations(@CurrentUser String userId) {
        return BaseResponse.onSuccess(relationManagementUseCase.findRelations(userId));
    }

    @PostMapping("/send")
    public BaseResponse<Void> sendRequest(@CurrentUser String userId, @RequestBody @Valid RelationConnectRequest request) {
        relationManagementUseCase.send(userId, request);
        return BaseResponse.onSuccess();
    }

    @PostMapping("/resend")
    public BaseResponse<Void> resendRequest(@CurrentUser String userId, @RequestBody @Valid RelationReconnectRequest request) {
        relationManagementUseCase.resend(userId, request);
        return BaseResponse.onSuccess();
    }

    @PatchMapping("/reply")
    public BaseResponse<Void> reply(@CurrentUser String userId, @RequestParam String relationId, @RequestParam RelationStatus status) {
        relationManagementUseCase.reply(userId, relationId, status);
        return BaseResponse.onSuccess();
    }

    @DeleteMapping
    public BaseResponse<Void> disconnect(@CurrentUser String userId, @RequestParam String relationId) {
        relationManagementUseCase.disconnect(userId, relationId);
        return BaseResponse.onSuccess();
    }
}
