package opensource.alzheimerdinger.core.domain.transcript.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TranscriptRequest {
    @NotBlank private String sessionId;
    @NotBlank private int sessionSeq;
    @NotBlank private LocalDateTime conversationDate;
    @NotBlank private String speaker;
    @NotBlank private String script;
}
