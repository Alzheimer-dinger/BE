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
        String averageCallTime // 임시값으로 지정되어 있는 상황 AI쪽 구현 후 수정 필요
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
