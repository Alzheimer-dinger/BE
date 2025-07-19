package opensource.alzheimerdinger.core.domain.transcript.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.ConversationEntry;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TranscriptRequest {
    private String sessionId;
    private String userId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<ConversationEntry> conversation;
}
