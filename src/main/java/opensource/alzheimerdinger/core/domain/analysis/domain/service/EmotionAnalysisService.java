package opensource.alzheimerdinger.core.domain.analysis.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.EmotionAnalysisRequest;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.EmotionAnalysis;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.ConversationSessionRepository;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.EmotionAnalysisRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmotionAnalysisService {
    private final ConversationSessionRepository sessionRepo;
    private final EmotionAnalysisRepository analysisRepo;

    @Transactional
    public void saveEmotionAnalysis(EmotionAnalysisRequest dto) {
        var session = sessionRepo.findById(dto.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid session"));
        var entity = EmotionAnalysis.builder()
                .analysisId(dto.getAnalysisId())
                .session(session)
                .analysisTime(dto.getAnalysisTime())
                .emotionLabel(dto.getEmotionLabel())
                .emotionScore(dto.getEmotionScore())
                .build();
        analysisRepo.save(entity);
    }
}