package opensource.alzheimerdinger.core.domain.feedback.application.dto.request;

import jakarta.validation.constraints.NotNull;
import opensource.alzheimerdinger.core.domain.feedback.domain.entity.Rating;

public record SaveFeedbackRequest (
        @NotNull Rating rating,
        @NotNull String reason
) {}
