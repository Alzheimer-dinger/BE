package opensource.alzheimerdinger.core.domain.analysis.domain.repository;

import opensource.alzheimerdinger.core.domain.analysis.domain.entity.EmotionAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmotionAnalysisRepository extends JpaRepository<EmotionAnalysis, String> {
    List<EmotionAnalysis> findBySessionId(String sessionId);
}