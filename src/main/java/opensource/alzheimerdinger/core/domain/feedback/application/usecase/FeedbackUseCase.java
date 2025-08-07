package opensource.alzheimerdinger.core.domain.feedback.application.usecase;

import io.micrometer.core.instrument.MeterRegistry;
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
    private final MeterRegistry registry;

    public void save(SaveFeedbackRequest request, String userId) {
        registry.counter("domain_feedback_save_requests").increment(); // 호출 횟수
        registry.timer("domain_feedback_save_duration", "domain", "feedback") // 실행 시간
                .record(() -> {
                    User user = userService.findUser(userId);
                    feedbackService.save(request, user);
                });
    }
}
