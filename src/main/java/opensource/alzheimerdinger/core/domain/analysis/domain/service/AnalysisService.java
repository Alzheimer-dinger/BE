package opensource.alzheimerdinger.core.domain.analysis.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisDayResponse;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.Analysis;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.AnalysisReport;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.AnalysisRepository;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.AnalysisReportRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final AnalysisReportRepository analysisReportRepository;

    public List<Analysis> findAnalysisData(String userId, LocalDateTime start, LocalDateTime end) {
        return analysisRepository.findByUserAndPeriod(userId, start, end);
    }

    public AnalysisResponse getPeriodData(String userId, LocalDate start, LocalDate end) {
        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);
        
        List<Analysis> analyses = findAnalysisData(userId, startDateTime, endDateTime);
        
        if (analyses.isEmpty()) {
            throw new RestApiException(_NOT_FOUND);
        }

        // 평균 위험 점수 계산
        Double averageRiskScore = analyses.stream()
                .mapToDouble(Analysis::getRiskScore)
                .average()
                .orElse(0.0);

        // 감정 타임라인 생성
        List<AnalysisResponse.EmotionDataPoint> emotionTimeline = analyses.stream()
                .map(analysis -> new AnalysisResponse.EmotionDataPoint(
                        analysis.getCreatedAt().toLocalDate(),
                        analysis.getHappy(),
                        analysis.getSad(),
                        analysis.getAngry(),
                        analysis.getSurprised(),
                        analysis.getBored(),
                        analysis.getRiskScore()
                ))
                .toList();

        return new AnalysisResponse(
                userId,
                start,
                end,
                averageRiskScore,
                emotionTimeline,
                analyses.size(), // totalParticipate
                "11분 20초" // averageCallTime
        );
    }

    public AnalysisDayResponse getDayData(String userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        
        List<Analysis> dayAnalyses = findAnalysisData(userId, startOfDay, endOfDay);
        
        if (dayAnalyses.isEmpty()) {
            throw new RestApiException(_NOT_FOUND);
        }

        Analysis latestAnalysis = dayAnalyses.get(dayAnalyses.size() - 1);
        
        //월간 데이터 생성 (달력용 - 해당 월의 모든 일별 요약)
        List<AnalysisDayResponse.EmotionSummary> monthlyData = getMonthlyEmotion(userId, date);

        return new AnalysisDayResponse(
                userId,
                date,
                latestAnalysis.getHappy(),
                latestAnalysis.getSad(),
                latestAnalysis.getAngry(),
                latestAnalysis.getSurprised(),
                latestAnalysis.getBored(),
                monthlyData
        );
    }

     //달력 UI용 월간 감정 요약 데이터 생성

    private List<AnalysisDayResponse.EmotionSummary> getMonthlyEmotion(String userId, LocalDate date) {
        // 해당 월의 첫날과 마지막날 계산
        LocalDateTime startOfMonth = date.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = date.withDayOfMonth(date.lengthOfMonth()).atTime(23, 59, 59);
        
        // 해당 월의 모든 분석 데이터 조회
        List<Analysis> monthlyAnalyses = findAnalysisData(userId, startOfMonth, endOfMonth);
        
        // 날짜별로 그룹핑하여 각 날의 대표 감정 계산
        return monthlyAnalyses.stream()
                .collect(Collectors.groupingBy(
                    analysis -> analysis.getCreatedAt().toLocalDate() // 날짜별로 그룹핑
                ))
                .entrySet().stream()
                .map(entry -> {
                    LocalDate dailyDate = entry.getKey();
                    List<Analysis> dailyAnalyses = entry.getValue();
                    
                    // 해당 날의 마지막 분석 데이터에서 주요 감정 추출
                    Analysis lastAnalysisOfDay = dailyAnalyses.get(dailyAnalyses.size() - 1);
                    String mainEmotion = getMainEmotion(lastAnalysisOfDay);
                    
                    return new AnalysisDayResponse.EmotionSummary(
                            dailyDate,
                            mainEmotion
                    );
                })
                .sorted((a, b) -> a.date().compareTo(b.date())) // 날짜순 정렬
                .toList();
    }

    public AnalysisReport findLatestReport(String userId, LocalDate periodEnd) {
        LocalDateTime periodEndDateTime = periodEnd.atTime(23, 59, 59);
        return analysisReportRepository.findLatestReport(userId, periodEndDateTime)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));
    }

    private String getMainEmotion(Analysis analysis) {
        double happy = analysis.getHappy();
        double sad = analysis.getSad();
        double angry = analysis.getAngry();
        double surprised = analysis.getSurprised();
        double bored = analysis.getBored();
        
        double maxScore = Math.max(happy, Math.max(sad, Math.max(angry, Math.max(surprised, bored))));
        
        if (maxScore == happy) return "happy";
        if (maxScore == sad) return "sad";
        if (maxScore == angry) return "angry";
        if (maxScore == surprised) return "surprised";
        return "bored";
    }
}
