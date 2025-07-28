package opensource.alzheimerdinger.core.domain.analysis.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AnalysisRequest(
        @NotEmpty String userId,
        @NotNull LocalDate start,
        @NotNull LocalDate end
) {}
