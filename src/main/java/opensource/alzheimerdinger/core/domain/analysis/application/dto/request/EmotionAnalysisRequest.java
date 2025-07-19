package opensource.alzheimerdinger.core.domain.analysis.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmotionAnalysisRequest {
    private String analysisId;
    private String sessionId;
    private LocalDateTime timestamp;

    private float happy;
    private float sad;
    private float angry;
    private float surprised;
    private float bored;
}
