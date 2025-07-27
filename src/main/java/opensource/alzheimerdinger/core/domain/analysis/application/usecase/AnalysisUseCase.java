package opensource.alzheimerdinger.core.domain.analysis.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.AnalysisRequest;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.AnalysisReportRequest;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisDayResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisReportResponse;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.AnalysisReport;
import opensource.alzheimerdinger.core.domain.analysis.domain.service.AnalysisService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AnalysisUseCase {

    private final AnalysisService analysisService;


    //특정 기간 감정 분석 데이터 조회
    public AnalysisResponse getAnalysisPeriodData(AnalysisRequest request) {
        return analysisService.getPeriodData(
                request.userId(),
                request.start(),
                request.end()
        );
    }


    //일별 감정 분석 데이터 조회 (달력용 데이터 포함)
    public AnalysisDayResponse getDayAnalysisData(String userId, LocalDateTime date) {
        return analysisService.getDayData(userId, date);
    }

    //기존 분석 리포트 중 가장 최근 리포트 조회

    public AnalysisReportResponse getLatestReport(AnalysisReportRequest request) {
        AnalysisReport latestReport = analysisService.findLatestReport(
                request.userId(),
                request.periodEnd()
        );
        
        return new AnalysisReportResponse(
                latestReport.getAnalysisReportId(),
                request.userId(),
                latestReport.getCreatedAt(),
                latestReport.getReport()
        );
    }
}
