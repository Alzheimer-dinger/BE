package opensource.alzheimerdinger.core.domain.transcript.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TranscriptRequest {
    @NotBlank private String sessionId;
    @NotBlank private int sessionSeq;
    @NotBlank private LocalDate conversationDate;
    @NotBlank private String speaker;
    @NotBlank private String script;
    @NotBlank private String timestamp;
}
