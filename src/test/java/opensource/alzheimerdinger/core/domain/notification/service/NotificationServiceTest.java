package opensource.alzheimerdinger.core.domain.notification.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import opensource.alzheimerdinger.core.domain.notification.entity.Notification;
import opensource.alzheimerdinger.core.domain.notification.repository.NotificationRepository;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private MockedStatic<FirebaseMessaging> firebaseMessagingStatic;
    private FirebaseMessaging fakeFirebaseMessaging;

    private final String token = "test-token";
    private final String title = "hello";
    private final String body = "world";
    private final String userId = "user-1";
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        when(userService.findUser(userId)).thenReturn(user);

        firebaseMessagingStatic = Mockito.mockStatic(FirebaseMessaging.class);
        fakeFirebaseMessaging = mock(FirebaseMessaging.class);
        firebaseMessagingStatic.when(FirebaseMessaging::getInstance).thenReturn(fakeFirebaseMessaging);
    }

    @AfterEach
    void tearDown() {
        firebaseMessagingStatic.close();
    }

    @Test
    void sendNotification_success_returnsMessageId_and_savesNotification() throws Exception {
        String expectedMessageId = "msg-123";
        when(fakeFirebaseMessaging.send(any(Message.class))).thenReturn(expectedMessageId);

        String result = notificationService.sendNotification(token, title, body, userId);

        assertThat(result).isEqualTo(expectedMessageId);
        verify(notificationRepository, times(1)).save(argThat(n ->
                title.equals(n.getTitle()) &&
                        body.equals(n.getBody()) &&
                        user.equals(n.getUser())
        ));
    }

    @Test
    void sendNotification_invalidArgument_returnsErrorCodeString() throws Exception {
        // FirebaseMessagingException을 직접 만들 수 없으므로 mock으로 만듦
        FirebaseMessagingException mockedException = mock(FirebaseMessagingException.class);
        when(mockedException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.INVALID_ARGUMENT);
        when(fakeFirebaseMessaging.send(any(Message.class))).thenThrow(mockedException);

        String result = notificationService.sendNotification(token, title, body, userId);

        assertThat(result).isEqualTo(MessagingErrorCode.INVALID_ARGUMENT.toString());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void sendNotification_unregistered_returnsErrorCodeString() throws Exception {
        FirebaseMessagingException mockedException = mock(FirebaseMessagingException.class);
        when(mockedException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);
        when(fakeFirebaseMessaging.send(any(Message.class))).thenThrow(mockedException);

        String result = notificationService.sendNotification(token, title, body, userId);

        assertThat(result).isEqualTo(MessagingErrorCode.UNREGISTERED.toString());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void sendNotification_otherError_throwsRuntimeException() throws Exception {
        FirebaseMessagingException mockedException = mock(FirebaseMessagingException.class);
        when(mockedException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.INTERNAL);
        when(fakeFirebaseMessaging.send(any(Message.class))).thenThrow(mockedException);

        assertThatThrownBy(() -> notificationService.sendNotification(token, title, body, userId))
                .isInstanceOf(RuntimeException.class)
                .hasCause(mockedException);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }
}
