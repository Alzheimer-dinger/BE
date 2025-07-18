package opensource.alzheimerdinger.core.domain.feedback.ui;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackUseCase feedbackUseCase;

    @PostMapping
    public BaseResponse<Void> save(@Valid @RequestBody SaveFeedbackRequest request, @CurrentUser String userId) {
        feedbackUseCase.save(request, userId);
        return BaseResponse.onSuccess();
    }
}
