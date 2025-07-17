package opensource.alzheimerdinger.core.domain.analysis.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CognitiveReportRequest {
    @NotBlank private String reportId;
    @NotBlank private String sessionId;
    @NotBlank private Integer riskScore;
    @NotBlank private String riskLevel;
    @NotBlank private String interpretation;
    @NotBlank private LocalDateTime createdAt;
}
