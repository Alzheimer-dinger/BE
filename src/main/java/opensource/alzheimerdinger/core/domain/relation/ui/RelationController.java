package opensource.alzheimerdinger.core.domain.relation.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Relation", description = "보호자-피보호자 관계 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/relations")
@SecurityRequirement(name = "Bearer Authentication")
public class RelationController {

    private final RelationManagementUseCase relationManagementUseCase;

    @Operation(
            summary = "관계 목록 조회",
            description = "현재 사용자의 보호자/피보호자 관계 목록을 반환",
            responses = @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = RelationResponse.class)))
    )
    @GetMapping
    public BaseResponse<List<RelationResponse>> getRelations(
            @Parameter(hidden = true) @CurrentUser String userId) {
        return BaseResponse.onSuccess(relationManagementUseCase.findRelations(userId));
    }

    @Operation(
            summary = "관계 요청 전송",
            description = "다른 사용자에게 보호자/피보호자 연결 요청을 보냄",
            requestBody = @RequestBody(
                    description = "연결 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RelationConnectRequest.class))
            ),
            responses = @ApiResponse(responseCode = "200", description = "요청 전송 성공")
    )
    @PostMapping("/send")
    public BaseResponse<Void> sendRequest(
            @Parameter(hidden = true) @CurrentUser String userId,
            @Valid @RequestBody RelationConnectRequest request) {
        relationManagementUseCase.send(userId, request);
        return BaseResponse.onSuccess();
    }

    @Operation(
            summary = "관계 요청 재전송",
            description = "만료된 관계 요청을 다시 보냄",
            requestBody = @RequestBody(
                    description = "재전송 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RelationReconnectRequest.class))
            ),
            responses = @ApiResponse(responseCode = "200", description = "재전송 성공")
    )
    @PostMapping("/resend")
    public BaseResponse<Void> resendRequest(
            @Parameter(hidden = true) @CurrentUser String userId,
            @Valid @RequestBody RelationReconnectRequest request) {
        relationManagementUseCase.resend(userId, request);
        return BaseResponse.onSuccess();
    }

    @Operation(
            summary = "관계 요청 응답",
            description = "보호자/피보호자 요청에 대해 승인 또는 거절",
            responses = @ApiResponse(responseCode = "200", description = "응답 성공")
    )
    @PatchMapping("/reply")
    public BaseResponse<Void> reply(
            @Parameter(hidden = true) @CurrentUser String userId,
            @RequestParam String relationId,
            @RequestParam RelationStatus status) {
        relationManagementUseCase.reply(userId, relationId, status);
        return BaseResponse.onSuccess();
    }

    @Operation(
            summary = "관계 해제",
            description = "현재 보호자/피보호자 관계를 해제",
            responses = @ApiResponse(responseCode = "200", description = "관계 해제 성공")
    )
    @DeleteMapping
    public BaseResponse<Void> disconnect(
            @Parameter(hidden = true) @CurrentUser String userId,
            @RequestParam String relationId) {
        relationManagementUseCase.disconnect(userId, relationId);
        return BaseResponse.onSuccess();
    }
}