package opensource.alzheimerdinger.core.domain.analysis.application.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AnalysisResponse(
        String userId,
        LocalDate start,
        LocalDate end,

        Double averageRiskScore,

        List<EmotionDataPoint> emotionTimeline,
        
        Integer totalParticipate,
        String averageCallTime // 임시값으로 지정되어 있는 상황 AI쪽 구현 후 수정 필요
) {    
    public record EmotionDataPoint(
            LocalDate date,

            Double happyScore,
            Double sadScore,
            Double angryScore,
            Double surprisedScore,
            Double boredScore,

            Double riskScore
    ) {}
}
