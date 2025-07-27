package opensource.alzheimerdinger.core.domain.analysis.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record AnalysisResponse(
        String userId,
        LocalDateTime start,
        LocalDateTime end,

        Float averageRiskScore,

        List<EmotionDataPoint> emotionTimeline,
        
        Integer totalParticipate,
        String averageCallTime
) {    
    public record EmotionDataPoint(
            LocalDateTime date,

            Float happyScore,
            Float sadScore,
            Float angryScore,
            Float surprisedScore,
            Float boredScore,

            Float riskScore
    ) {}
}
