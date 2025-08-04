package opensource.alzheimerdinger.core.domain.notification.service;

import opensource.alzheimerdinger.core.domain.notification.entity.FcmToken;
import opensource.alzheimerdinger.core.domain.notification.repository.FcmTokenRepository;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcmTokenServiceTest {

    @Mock
    private FcmTokenRepository fcmTokenRepository;

    @InjectMocks
    private FcmTokenService fcmTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void upsert_whenNoExistingToken_createsAndSavesNew() {
        // given
        String newToken = "token-abc";
        when(fcmTokenRepository.findByUser(user)).thenReturn(Optional.empty());

        // when
        fcmTokenService.upsert(user, newToken);

        // then
        ArgumentCaptor<FcmToken> captor = ArgumentCaptor.forClass(FcmToken.class);
        verify(fcmTokenRepository, times(1)).save(captor.capture());

        FcmToken saved = captor.getValue();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getToken()).isEqualTo(newToken);
    }

    @Test
    void upsert_whenExistingToken_updatesAndSaves() {
        // given
        String oldToken = "old-token";
        String updatedToken = "new-token";

        FcmToken existing = FcmToken.builder()
                .token(oldToken)
                .user(user)
                .build();

        when(fcmTokenRepository.findByUser(user)).thenReturn(Optional.of(existing));

        // spy 형태로 기존 객체의 updateToken 호출이 반영되도록, 여기선 real object라서 그대로 사용
        // when
        fcmTokenService.upsert(user, updatedToken);

        // then
        // 기존 객체의 토큰이 갱신돼야 하고 save 호출
        assertThat(existing.getToken()).isEqualTo(updatedToken);
        verify(fcmTokenRepository, times(1)).save(existing);
    }

    @Test
    void expire_callsRepositoryExpire() {
        // given
        String userId = "user-xyz";

        // when
        fcmTokenService.expire(userId);

        // then
        verify(fcmTokenRepository, times(1)).expire(userId);
    }
}
