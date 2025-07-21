package opensource.alzheimerdinger.core.domain.batch.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

//배치 실행 요청 DTO (특정 유저 처리용)
public record TranscriptBatchRequest(
        @NotBlank(message = "유저 ID는 필수입니다")
        String userId,                      // 처리할 유저 ID (필수)
        @NotNull(message = "시작 날짜는 필수입니다")
        LocalDateTime fromDate,             // 시작 날짜/시간 (포함)
        @NotNull(message = "종료 날짜는 필수입니다")
        LocalDateTime toDate                // 종료 날짜/시간 (미포함)
) {
} 