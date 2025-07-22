package opensource.alzheimerdinger.core.domain.feedback.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.feedback.application.dto.request.SaveFeedbackRequest;
import opensource.alzheimerdinger.core.domain.feedback.domain.service.FeedbackService;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackUseCase {

    private final UserService userService;
    private final FeedbackService feedbackService;

    public void save(SaveFeedbackRequest request, String userId) {
        User user = userService.findUser(userId);
        feedbackService.save(request, user);
    }
}
