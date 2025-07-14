package opensource.alzheimerdinger.core.domain.batch.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record XXXBatchRequest(
        @NotBlank String date,
        List<String> targetIds,
        String processedBy
) {}