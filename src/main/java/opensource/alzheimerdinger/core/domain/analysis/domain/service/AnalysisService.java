package opensource.alzheimerdinger.core.domain.analysis.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisDayResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisMonthlyResponse;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.EmotionAnalysis;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.AnalysisReport;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.DementiaAnalysis;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.EmotionAnalysisRepository;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.DementiaAnalysisRepository;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.AnalysisReportRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final EmotionAnalysisRepository emotionAnalysisRepository;
    private final DementiaAnalysisRepository dementiaAnalysisRepository;
    private final AnalysisReportRepository analysisReportRepository;

    public List<EmotionAnalysis> findEmotionAnalysisData(String userId, LocalDateTime start, LocalDateTime end) {
        return emotionAnalysisRepository.findByUserAndPeriod(userId, start, end);
    }

    public AnalysisResponse getPeriodData(String userId, LocalDate start, LocalDate end) {
        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);
        
        List<EmotionAnalysis> analyses = findEmotionAnalysisData(userId, startDateTime, endDateTime);
        
        if (analyses.isEmpty()) {
            throw new RestApiException(_NOT_FOUND);
        }

        // 같은 기간의 치매 분석 데이터 조회
        List<DementiaAnalysis> dementiaAnalyses = dementiaAnalysisRepository.findByUserAndPeriod(userId, startDateTime, endDateTime);

        // 평균 위험 점수 계산
        Double averageRiskScore = dementiaAnalyses.stream()
                .mapToDouble(DementiaAnalysis::getRiskScore)
                .average()
                .orElse(0.0);

        // 매핑 준비: sessionId -> riskScore, date -> 평균 riskScore
        Map<String, Double> sessionIdToRisk = dementiaAnalyses.stream()
                .collect(Collectors.toMap(
                        DementiaAnalysis::getSessionId,
                        DementiaAnalysis::getRiskScore,
                        (existing, replacement) -> replacement
                ));

        Map<LocalDate, Double> dateToAverageRisk = dementiaAnalyses.stream()
                .collect(Collectors.groupingBy(
                        da -> da.getCreatedAt().toLocalDate(),
                        Collectors.averagingDouble(DementiaAnalysis::getRiskScore)
                ));

        // 감정 타임라인 생성
        List<AnalysisResponse.EmotionDataScore> emotionTimeline = analyses.stream()
                .map(analysis -> {
                    LocalDate date = analysis.getCreatedAt().toLocalDate();

                    Double risk = sessionIdToRisk.get(analysis.getSessionId());
                    if (risk == null) {
                        risk = dateToAverageRisk.get(date);
                    }
                    return new AnalysisResponse.EmotionDataScore(
                            date,
                            analysis.getHappy(),
                            analysis.getSad(),
                            analysis.getAngry(),
                            analysis.getSurprised(),
                            analysis.getBored(),
                            risk
                    );
                })
                .toList();

        return new AnalysisResponse(
                userId,
                start,
                end,
                averageRiskScore,
                emotionTimeline,
                analyses.size(), // totalParticipate
                "11분 20초" // averageCallTime 임시값으로 지정되어 있는 상황 AI쪽 구현 후 수정 필요
        );
    }

    public AnalysisDayResponse getDayData(String userId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        
        List<EmotionAnalysis> dayAnalyses = findEmotionAnalysisData(userId, startOfDay, endOfDay);
        
        if (dayAnalyses.isEmpty()) {
            return new AnalysisDayResponse(
                    userId,
                    date,
                    false,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }

        EmotionAnalysis latestAnalysis = dayAnalyses.get(dayAnalyses.size() - 1);

        return new AnalysisDayResponse(
                userId,
                date,
                true,
                latestAnalysis.getHappy(),
                latestAnalysis.getSad(),
                latestAnalysis.getAngry(),
                latestAnalysis.getSurprised(),
                latestAnalysis.getBored()
        );
    }

    // 달력 UI용 월간 감정 요약 데이터 생성 (데이터가 없어도 빈 리스트로 반환)
    public AnalysisMonthlyResponse getMonthlyData(String userId, LocalDate date) {
        List<AnalysisMonthlyResponse.EmotionSummary> monthlyData = getMonthlyEmotion(userId, date);
        LocalDate normalizedMonth = date.withDayOfMonth(1);
        return new AnalysisMonthlyResponse(userId, normalizedMonth, monthlyData);
    }

    private List<AnalysisMonthlyResponse.EmotionSummary> getMonthlyEmotion(String userId, LocalDate date) {
        // 해당 월의 첫날과 마지막날 계산
        LocalDateTime startOfMonth = date.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = date.withDayOfMonth(date.lengthOfMonth()).atTime(23, 59, 59);
        
        // 해당 월의 모든 분석 데이터 조회
        List<EmotionAnalysis> monthlyAnalyses = findEmotionAnalysisData(userId, startOfMonth, endOfMonth);
        
        // 날짜별로 그룹핑하여 각 날의 대표 감정 계산
        return monthlyAnalyses.stream()
                .collect(Collectors.groupingBy(
                    analysis -> analysis.getCreatedAt().toLocalDate() // 날짜별로 그룹핑
                ))
                .entrySet().stream()
                .map(entry -> {
                    LocalDate dailyDate = entry.getKey();
                    List<EmotionAnalysis> dailyAnalyses = entry.getValue();
                    
                    // 해당 날의 마지막 분석 데이터에서 주요 감정 추출
                    EmotionAnalysis lastAnalysisOfDay = dailyAnalyses.get(dailyAnalyses.size() - 1);
                    String mainEmotion = getMainEmotion(lastAnalysisOfDay);
                    
                    return new AnalysisMonthlyResponse.EmotionSummary(
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

    private String getMainEmotion(EmotionAnalysis analysis) {
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
