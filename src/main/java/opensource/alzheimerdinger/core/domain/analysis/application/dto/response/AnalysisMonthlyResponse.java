package opensource.alzheimerdinger.core.domain.analysis.application.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AnalysisMonthlyResponse(
        String userId,
        LocalDate month,
        List<EmotionSummary> monthlyEmotionData
) {
    public record EmotionSummary(
            LocalDate date,
            String emotionType
    ) {}
}


