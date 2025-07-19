package opensource.alzheimerdinger.core.domain.analysis.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.CognitiveReport;

import java.time.LocalDateTime;

@Data
public class CognitiveReportRequest {
    private String reportId;
    private String sessionId;

    private float riskScore;
    private CognitiveReport.RiskLabel riskLabel;

    private LocalDateTime createdAt;
}
