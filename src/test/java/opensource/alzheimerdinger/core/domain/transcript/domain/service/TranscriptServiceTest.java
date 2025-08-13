package opensource.alzheimerdinger.core.domain.transcript.domain.service;

import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptDetailResponse;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptListResponse;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Speaker;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.TranscriptMessage;
import opensource.alzheimerdinger.core.domain.transcript.domain.repository.TranscriptRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranscriptServiceTest {

    @Mock
    TranscriptRepository transcriptRepository;

    @InjectMocks
    TranscriptService transcriptService;

    @Test
    void getList_success() {
        // Given
        String userId = "user-1";
        Instant start = Instant.parse("2024-07-30T00:30:00Z");
        Instant end = start.plusSeconds(730);

        Transcript t = Transcript.builder()
                .sessionId("sess_20240730_0030")
                .userId(userId)
                .startTime(start)
                .endTime(end)
                .summary("요약")
                .build();

        when(transcriptRepository.findByUser(userId)).thenReturn(List.of(t));

        // When
        List<TranscriptListResponse> result = transcriptService.getList(userId);

        // Then
        assertThat(result).hasSize(1);
        TranscriptListResponse item = result.get(0);
        assertThat(item.sessionId()).isEqualTo("sess_20240730_0030");
        assertThat(item.durationSeconds()).isEqualTo("730");
        verify(transcriptRepository).findByUser(userId);
    }

    @Test
    void getList_fail_no_data() {
        // Given
        String userId = "user-1";
        when(transcriptRepository.findByUser(userId)).thenReturn(List.of());

        // When
        Throwable thrown = catchThrowable(() -> transcriptService.getList(userId));

        // Then
        assertThat(thrown).isInstanceOf(RestApiException.class);
        assertThat(((RestApiException) thrown).getErrorCode()).isEqualTo(_NOT_FOUND.getCode());
    }

    @Test
    void getDetail_success() {
        // Given
        String userId = "user-1";
        String sessionId = "sess-1";
        Instant start = Instant.parse("2024-07-30T09:30:00Z");
        Instant end = start.plusSeconds(600);

        TranscriptMessage m1 = new TranscriptMessage(Speaker.patient, "안녕하세요");
        TranscriptMessage m2 = new TranscriptMessage(Speaker.ai, "오늘 기분은 어떠세요?");

        Transcript t = Transcript.builder()
                .sessionId(sessionId)
                .userId(userId)
                .startTime(start)
                .endTime(end)
                .summary("기분과 수면 상태에 대한 대화")
                .conversation(List.of(m1, m2))
                .build();

        when(transcriptRepository.findBySessionId(sessionId)).thenReturn(Optional.of(t));

        // When
        TranscriptDetailResponse detail = transcriptService.getDetail(userId, sessionId);

        // Then
        assertThat(detail.sessionId()).isEqualTo(sessionId);
        assertThat(detail.summary()).isEqualTo("기분과 수면 상태에 대한 대화");
        assertThat(detail.durationSeconds()).isEqualTo("600");
        assertThat(detail.conversation()).hasSize(2);
        assertThat(detail.conversation().get(0).speaker()).isEqualTo(Speaker.patient);
        assertThat(detail.conversation().get(1).speaker()).isEqualTo(Speaker.ai);
        verify(transcriptRepository).findBySessionId(sessionId);
    }

    @Test
    void getDetail_fail_wrong_user_or_not_found() {
        // Given: 세션은 존재하나 userId가 다름 → 필터로 인해 Optional 비게 됨
        String requestedUserId = "user-A";
        String sessionId = "sess-X";
        Transcript t = Transcript.builder()
                .sessionId(sessionId)
                .userId("user-B")
                .startTime(Instant.parse("2024-06-01T10:00:00Z"))
                .endTime(Instant.parse("2024-06-01T10:10:00Z"))
                .build();
        when(transcriptRepository.findBySessionId(sessionId)).thenReturn(Optional.of(t));

        // When
        Throwable thrown = catchThrowable(() -> transcriptService.getDetail(requestedUserId, sessionId));

        // Then
        assertThat(thrown).isInstanceOf(RestApiException.class);
        assertThat(((RestApiException) thrown).getErrorCode()).isEqualTo(_NOT_FOUND.getCode());
    }
}


