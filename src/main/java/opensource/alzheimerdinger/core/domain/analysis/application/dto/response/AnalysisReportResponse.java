package opensource.alzheimerdinger.core.domain.analysis.application.dto.response;

import java.time.LocalDate;

public record AnalysisReportResponse(
        String reportId,
        String userId,
        LocalDate createdAt,
        String report
) {}
