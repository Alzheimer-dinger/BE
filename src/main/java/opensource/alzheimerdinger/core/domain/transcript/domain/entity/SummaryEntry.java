package opensource.alzheimerdinger.core.domain.transcript.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SummaryEntry {
    private LocalDateTime timestamp; // 요약 시점
    private String summary; // 요약 내용
}