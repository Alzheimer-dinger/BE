package opensource.alzheimerdinger.core.domain.analysis.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AnalysisReportRequest(
        @NotEmpty String userId,
        @NotNull LocalDate periodEnd
) {}
