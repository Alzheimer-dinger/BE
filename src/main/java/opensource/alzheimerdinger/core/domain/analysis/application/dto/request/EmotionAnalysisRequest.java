package opensource.alzheimerdinger.core.domain.analysis.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmotionAnalysisRequest {
    @NotBlank private String analysisId;
    @NotBlank private String sessionId;
    @NotBlank private LocalDateTime analysisTime;
    @NotBlank private String emotionLabel;
    @NotBlank private Float emotionScore;
}
