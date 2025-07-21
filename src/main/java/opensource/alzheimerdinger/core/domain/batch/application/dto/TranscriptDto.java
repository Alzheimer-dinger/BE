package opensource.alzheimerdinger.core.domain.batch.application.dto;

import opensource.alzheimerdinger.core.domain.transcript.domain.entity.ConversationEntry;

import java.time.LocalDateTime;
import java.util.List;

//Transcript entity의 전체 정보를 담은 DTO (Kafka 전송용)
public record TranscriptDto(
        String transcriptId,
        String sessionId,
        String userId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<ConversationEntry> conversation
) {
} 