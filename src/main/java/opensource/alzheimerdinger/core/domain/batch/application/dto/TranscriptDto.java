package opensource.alzheimerdinger.core.domain.batch.application.dto;

import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Speaker;

import java.time.LocalDateTime;

//Transcript entity의 모든 정보를 담은 DTO
public record TranscriptDto(
        String id,
        String sessionId,
        int sessionSeq,
        LocalDateTime conversationDate,
        Speaker speaker,
        String script
) {
} 