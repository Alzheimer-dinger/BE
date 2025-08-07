package opensource.alzheimerdinger.core.domain.feedback.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.feedback.application.dto.request.SaveFeedbackRequest;
import opensource.alzheimerdinger.core.domain.feedback.application.usecase.FeedbackUseCase;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Feedback", description = "피드백 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedback")
@SecurityRequirement(name = "Bearer Authentication")
public class FeedbackController {

    private final FeedbackUseCase feedbackUseCase;

    @Operation(
            summary = "피드백 저장",
            description = "사용자가 서비스 이용 중 작성한 피드백을 저장",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "저장할 피드백 데이터",
                    required = true,
                    content = @Content(schema = @Schema(implementation = SaveFeedbackRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "피드백 저장 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
            }
    )
    @PostMapping
    public BaseResponse<Void> save(
            @Valid @RequestBody SaveFeedbackRequest request,
            @Parameter(hidden = true) @CurrentUser String userId) {
        feedbackUseCase.save(request, userId);
        return BaseResponse.onSuccess();
    }
}