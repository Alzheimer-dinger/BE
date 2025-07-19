package opensource.alzheimerdinger.core.domain.batch.application.dto.request;

import java.util.List;

//배치 실행 요청 DTO
public record TranscriptBatchRequest(
        String date, //배치 실행 기준 날짜
        List<String> targetIds //특정 ID들만 처리할 경우
) {
} 