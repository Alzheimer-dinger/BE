package opensource.alzheimerdinger.core.domain.analysis.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AnalysisDayResponse(
        String userId,
        LocalDateTime analysisDate,
        
        Float happyScore,
        Float sadScore,
        Float angryScore,
        Float surprisedScore,
        Float boredScore,
        
        List<EmotionSummary> monthlyEmotionData
) {
    
    public record EmotionSummary(
            LocalDateTime date,
            String emotionType
    ) {}
}