package opensource.alzheimerdinger.core.domain.feedback.application.usecase;

import opensource.alzheimerdinger.core.domain.feedback.application.dto.request.SaveFeedbackRequest;
import opensource.alzheimerdinger.core.domain.feedback.domain.service.FeedbackService;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackUseCaseTest {

    @Mock  UserService userService;
    @Mock  FeedbackService feedbackService;
    @InjectMocks  FeedbackUseCase feedbackUseCase;

    @Test
    void save_success() {
        // given
        String userId = "user-1";
        User user = mock(User.class);
        SaveFeedbackRequest req = mock(SaveFeedbackRequest.class);
        when(userService.findUser(userId)).thenReturn(user);

        // when
        feedbackUseCase.save(req, userId);

        // then
        verify(userService).findUser(userId);
        verify(feedbackService).save(req, user);
        verifyNoMoreInteractions(userService, feedbackService);
    }
}
