package opensource.alzheimerdinger.core.domain.batch.application.dto.response;

import java.util.List;

//배치 실행 결과 응답 DTO
public record TranscriptBatchResponse(
        String jobId,
        String status,
        String message,
        Integer processedCount,
        List<String> processedIds,
        Long executionTime
) {
} 