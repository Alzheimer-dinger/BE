package opensource.alzheimerdinger.core.domain.analysis.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AnalysisRequest(
        @NotNull String userId,
        @NotNull LocalDateTime start,
        @NotNull LocalDateTime end
) {}
