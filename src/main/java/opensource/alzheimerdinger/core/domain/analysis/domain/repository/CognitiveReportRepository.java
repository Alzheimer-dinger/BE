package opensource.alzheimerdinger.core.domain.analysis.domain.repository;


import opensource.alzheimerdinger.core.domain.analysis.domain.entity.CognitiveReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CognitiveReportRepository
        extends JpaRepository<CognitiveReport, String> {
    List<CognitiveReport> findBySession_SessionId(String sessionId);
}