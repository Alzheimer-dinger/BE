package opensource.alzheimerdinger.core.domain.analysis.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.CognitiveReportRequest;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.CognitiveReport;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.CognitiveReportRepository;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.ConversationSessionRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CognitiveReportService {
    private final ConversationSessionRepository sessionRepo;
    private final CognitiveReportRepository reportRepo;

    @Transactional
    public void saveCognitiveReport(CognitiveReportRequest dto) {
        var session = sessionRepo.findById(dto.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid session"));
        var entity = CognitiveReport.builder()
                .reportId(dto.getReportId())
                .session(session)
                .riskScore(dto.getRiskScore())
                .riskLevel(dto.getRiskLevel())
                .interpretation(dto.getInterpretation())
                .createdAt(dto.getCreatedAt())
                .build();
        reportRepo.save(entity);
    }
}