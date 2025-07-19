package opensource.alzheimerdinger.core.domain.analysis.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.EmotionAnalysisRequest;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.EmotionAnalysis;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.EmotionAnalysisRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmotionAnalysisService {
    private final EmotionAnalysisRepository analysisRepo;

    @Transactional
    public EmotionAnalysis saveEmotionAnalysis(EmotionAnalysisRequest dto) {
        EmotionAnalysis entity = EmotionAnalysis.builder()
                .analysisId(dto.getAnalysisId())
                .sessionId(dto.getSessionId())
                .timestamp(dto.getTimestamp())
                .happy(dto.getHappy())
                .sad(dto.getSad())
                .angry(dto.getAngry())
                .surprised(dto.getSurprised())
                .bored(dto.getBored())
                .build();
        return analysisRepo.save(entity);
    }
}