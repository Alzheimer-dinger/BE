package opensource.alzheimerdinger.core.domain.analysis.application.dto.response;

import java.time.LocalDateTime;

public record AnalysisReportResponse(
        String reportId,
        String userId,
        LocalDateTime createdAt,
        String report
) {}
