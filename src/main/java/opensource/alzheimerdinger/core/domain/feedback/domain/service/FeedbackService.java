package opensource.alzheimerdinger.core.domain.feedback.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.feedback.application.dto.request.SaveFeedbackRequest;
import opensource.alzheimerdinger.core.domain.feedback.domain.entity.Feedback;
import opensource.alzheimerdinger.core.domain.feedback.domain.repository.FeedbackRepository;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;


    public void save(SaveFeedbackRequest request, User user) {
        feedbackRepository.save(
                Feedback.builder()
                        .rating(request.rating())
                        .reason(request.reason())
                        .user(user)
                        .build()
        );
    }
}
