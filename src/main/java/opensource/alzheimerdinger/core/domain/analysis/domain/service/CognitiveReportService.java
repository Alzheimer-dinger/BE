package opensource.alzheimerdinger.core.domain.analysis.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.CognitiveReportRequest;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.CognitiveReport;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.CognitiveReportRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CognitiveReportService {
    private final CognitiveReportRepository reportRepo;

    @Transactional
    public CognitiveReport saveCognitiveReport(CognitiveReportRequest dto) {
        CognitiveReport entity = CognitiveReport.builder()
                .reportId(dto.getReportId())
                .sessionId(dto.getSessionId())
                .riskScore(dto.getRiskScore())
                .riskLabel(dto.getRiskLabel())
                .createdAt(dto.getCreatedAt())
                .build();
        return reportRepo.save(entity);
    }
}