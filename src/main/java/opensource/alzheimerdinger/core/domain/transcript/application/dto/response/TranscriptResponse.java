package opensource.alzheimerdinger.core.domain.transcript.application.dto.response;

import lombok.Data;

@Data
public class TranscriptResponse {
    private String id;
    private String sessionId;
    private String speaker;
    private String script;
    private String timestamp;
}
