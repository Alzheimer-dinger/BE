package opensource.alzheimerdinger.core.domain.batch.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record XXXBatchResponse(
        String jobId,
        String status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Integer processedCount,
        List<String> processedIds,
        String message
) {} 