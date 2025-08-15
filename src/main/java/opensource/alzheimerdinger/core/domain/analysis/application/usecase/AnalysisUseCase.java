package opensource.alzheimerdinger.core.domain.analysis.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisDayResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisReportResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisMonthlyEmotionResponse;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.AnalysisReport;
import opensource.alzheimerdinger.core.domain.analysis.domain.service.AnalysisService;
import opensource.alzheimerdinger.core.global.metric.UseCaseMetric;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
public class AnalysisUseCase {

    private final AnalysisService analysisService;


    //특정 기간 감정 분석 데이터 조회
    @UseCaseMetric(domain = "analysis", value = "get-period", type = "query")
    public AnalysisResponse getAnalysisPeriodData(String userId, LocalDate start, LocalDate end) {
        return analysisService.getPeriodData(userId, start, end);
    }


    //일별 감정 분석 데이터 조회 (달력용 데이터 포함)
    @UseCaseMetric(domain = "analysis", value = "get-day", type = "query")
    public AnalysisDayResponse getAnalysisDayData(String userId, LocalDate date) {
        return analysisService.getDayData(userId, date);
    }

    // 월간 달력용 데이터 조회
    @UseCaseMetric(domain = "analysis", value = "get-month", type = "query")
    public AnalysisMonthlyEmotionResponse getAnalysisMonthlyEmotionData(String userId, LocalDate date) {
        return analysisService.getMonthlyEmotionData(userId, date);
    }

    //기존 분석 리포트 중 가장 최근 리포트 조회
    @UseCaseMetric(domain = "analysis", value = "get-latest-report", type = "query")
    public AnalysisReportResponse getLatestReport(String userId, LocalDate periodEnd) {
        AnalysisReport latestReport = analysisService.findLatestReport(userId, periodEnd);
        
        return new AnalysisReportResponse(
                latestReport.getAnalysisReportId(),
                userId,
                latestReport.getCreatedAt().toLocalDate(),
                latestReport.getContent()
        );
    }
}
