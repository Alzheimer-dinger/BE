package opensource.alzheimerdinger.core.domain.transcript.application.usecase;

import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptDetailResponse;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptListResponse;
import opensource.alzheimerdinger.core.domain.transcript.domain.service.TranscriptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranscriptUseCaseTest {

    @Mock
    TranscriptService transcriptService;

    @InjectMocks
    TranscriptUseCase transcriptUseCase;

    @Test
    void list_success() {
        // Given
        String userId = "user-1";
        TranscriptListResponse mockItem = new TranscriptListResponse(
                "sess-1", "타이틀", java.time.LocalDate.now(), java.time.LocalTime.NOON, java.time.LocalTime.NOON, "60"
        );
        when(transcriptService.getList(userId)).thenReturn(List.of(mockItem));

        // When
        List<TranscriptListResponse> result = transcriptUseCase.list(userId);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).sessionId()).isEqualTo("sess-1");
        verify(transcriptService).getList(userId);
    }

    @Test
    void detail_success() {
        // Given
        String userId = "user-1";
        String sessionId = "sess-1";
        TranscriptDetailResponse mockDetail = new TranscriptDetailResponse(
                sessionId, "타이틀", java.time.LocalDate.now(), java.time.LocalTime.NOON, java.time.LocalTime.NOON, "120", "요약", List.of()
        );
        when(transcriptService.getDetail(userId, sessionId)).thenReturn(mockDetail);

        // When
        TranscriptDetailResponse result = transcriptUseCase.detail(userId, sessionId);

        // Then
        assertThat(result.sessionId()).isEqualTo(sessionId);
        verify(transcriptService).getDetail(userId, sessionId);
    }
}


