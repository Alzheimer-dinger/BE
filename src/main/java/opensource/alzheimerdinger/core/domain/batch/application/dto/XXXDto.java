package opensource.alzheimerdinger.core.domain.batch.application.dto;

import java.time.LocalDateTime;

public record XXXDto(
        String id,
        String name,
        String status,
        LocalDateTime processedAt,
        String processedBy
) {} 