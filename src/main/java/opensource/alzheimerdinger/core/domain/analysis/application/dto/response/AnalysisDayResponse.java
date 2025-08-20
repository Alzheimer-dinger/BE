package opensource.alzheimerdinger.core.domain.analysis.application.dto.response;

import java.time.LocalDate;

public record AnalysisDayResponse(
        String userId,
        LocalDate analysisDate,
        boolean hasData,
        
        Double happyScore,
        Double sadScore,
        Double angryScore,
        Double surprisedScore,
        Double boredScore
) {
}