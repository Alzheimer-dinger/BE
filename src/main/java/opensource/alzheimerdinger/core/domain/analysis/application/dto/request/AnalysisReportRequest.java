package opensource.alzheimerdinger.core.domain.analysis.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AnalysisReportRequest(
        @NotNull String userId,
        @NotNull LocalDateTime periodEnd
) {}
