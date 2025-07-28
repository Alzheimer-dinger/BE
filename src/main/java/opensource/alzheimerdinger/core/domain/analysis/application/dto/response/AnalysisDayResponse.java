package opensource.alzheimerdinger.core.domain.analysis.application.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AnalysisDayResponse(
        String userId,
        LocalDate analysisDate,
        
        Double happyScore,
        Double sadScore,
        Double angryScore,
        Double surprisedScore,
        Double boredScore,
        
        List<EmotionSummary> monthlyEmotionData
) {
    
    public record EmotionSummary(
            LocalDate date,
            String emotionType
    ) {}
}