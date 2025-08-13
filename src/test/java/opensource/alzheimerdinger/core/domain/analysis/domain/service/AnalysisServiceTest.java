package opensource.alzheimerdinger.core.domain.analysis.domain.service;

import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisDayResponse;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.EmotionAnalysis;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.AnalysisReport;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.EmotionAnalysisRepository;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.DementiaAnalysisRepository;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.AnalysisReportRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    @Mock
    EmotionAnalysisRepository emotionAnalysisRepository;

    @Mock
    DementiaAnalysisRepository dementiaAnalysisRepository;

    @Mock
    AnalysisReportRepository analysisReportRepository;

    @InjectMocks
    AnalysisService analysisService;

    @Test
    void findAnalysisData_success() {
        // Given
        String userId = "user123";
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);
        
        EmotionAnalysis a1 = mock(EmotionAnalysis.class);
        EmotionAnalysis a2 = mock(EmotionAnalysis.class);
        List<EmotionAnalysis> expected = List.of(a1, a2);
        
        when(emotionAnalysisRepository.findByUserAndPeriod(userId, start, end)).thenReturn(expected);

        // When
        List<EmotionAnalysis> result = analysisService.findEmotionAnalysisData(userId, start, end);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(a1, a2);
        verify(emotionAnalysisRepository).findByUserAndPeriod(userId, start, end);
    }

    @Test
    void getPeriodData_success() {
        // Given
        String userId = "user123";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        
        EmotionAnalysis e1 = mock(EmotionAnalysis.class);
        when(e1.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(e1.getHappy()).thenReturn(0.8);
        when(e1.getSad()).thenReturn(0.1);
        when(e1.getAngry()).thenReturn(0.05);
        when(e1.getSurprised()).thenReturn(0.03);
        when(e1.getBored()).thenReturn(0.02);
        when(e1.getSessionId()).thenReturn("s-1");

        EmotionAnalysis e2 = mock(EmotionAnalysis.class);
        when(e2.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(e2.getHappy()).thenReturn(0.6);
        when(e2.getSad()).thenReturn(0.3);
        when(e2.getAngry()).thenReturn(0.06);
        when(e2.getSurprised()).thenReturn(0.02);
        when(e2.getBored()).thenReturn(0.02);
        when(e2.getSessionId()).thenReturn("s-2");

        when(emotionAnalysisRepository.findByUserAndPeriod(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(e1, e2));

        // 위험도 평균을 위해 치매 분석은 간단히 0.2, 0.4로 가정
        var d1 = mock(opensource.alzheimerdinger.core.domain.analysis.domain.entity.DementiaAnalysis.class);
        when(d1.getRiskScore()).thenReturn(0.2);
        when(d1.getSessionId()).thenReturn("s-1");
        when(d1.getCreatedAt()).thenReturn(LocalDateTime.now());
        var d2 = mock(opensource.alzheimerdinger.core.domain.analysis.domain.entity.DementiaAnalysis.class);
        when(d2.getRiskScore()).thenReturn(0.4);
        when(d2.getSessionId()).thenReturn("s-9");
        when(d2.getCreatedAt()).thenReturn(LocalDateTime.now());

        when(dementiaAnalysisRepository.findByUserAndPeriod(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(d1, d2));

        // When
        AnalysisResponse result = analysisService.getPeriodData(userId, start, end);

        // Then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.start()).isEqualTo(start);
        assertThat(result.end()).isEqualTo(end);
        assertThat(result.averageRiskScore()).isCloseTo(0.3, org.assertj.core.api.Assertions.within(1e-6)); // (0.2 + 0.4) / 2
        assertThat(result.emotionTimeline()).hasSize(2);
        assertThat(result.totalParticipate()).isEqualTo(2);
    }

    @Test
    void getPeriodData_fail_no_data() {
        // Given
        String userId = "user123";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        
        when(emotionAnalysisRepository.findByUserAndPeriod(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        // When
        Throwable thrown = catchThrowable(() -> analysisService.getPeriodData(userId, start, end));

        // Then
        assertThat(thrown)
                .isInstanceOf(RestApiException.class);
        assertThat(((RestApiException) thrown).getErrorCode())
                .isEqualTo(_NOT_FOUND.getCode());
    }

    @Test
    void getDayData_success() {
        // Given
        String userId = "user123";
        LocalDate date = LocalDate.of(2024, 1, 25);
        
        EmotionAnalysis e1 = mock(EmotionAnalysis.class);
        lenient().when(e1.getHappy()).thenReturn(0.7);
        lenient().when(e1.getSad()).thenReturn(0.2);
        lenient().when(e1.getAngry()).thenReturn(0.05);
        lenient().when(e1.getSurprised()).thenReturn(0.03);
        lenient().when(e1.getBored()).thenReturn(0.02);
        lenient().when(e1.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 25, 10, 0));
        
        EmotionAnalysis e2 = mock(EmotionAnalysis.class);
        lenient().when(e2.getHappy()).thenReturn(0.8);
        lenient().when(e2.getSad()).thenReturn(0.1);
        lenient().when(e2.getAngry()).thenReturn(0.05);
        lenient().when(e2.getSurprised()).thenReturn(0.03);
        lenient().when(e2.getBored()).thenReturn(0.02);
        lenient().when(e2.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 25, 15, 0));
        
        List<EmotionAnalysis> dayAnalyses = List.of(e1, e2);
        
        // 모든 findByUserAndPeriod 호출에 대해 같은 결과 반환
        lenient().when(emotionAnalysisRepository.findByUserAndPeriod(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(dayAnalyses);

        // When
        AnalysisDayResponse result = analysisService.getDayData(userId, date);

        // Then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.analysisDate()).isEqualTo(date);
        assertThat(result.happyScore()).isEqualTo(0.8);
        assertThat(result.sadScore()).isEqualTo(0.1);
        assertThat(result.monthlyEmotionData()).isNotNull();
    }

    @Test
    void findLatestReport_success() {
        // Given
        String userId = "user123";
        LocalDate periodEnd = LocalDate.of(2024, 1, 31);
        
        AnalysisReport mockReport = mock(AnalysisReport.class);
        when(mockReport.getAnalysisReportId()).thenReturn("report123");
        when(mockReport.getContent()).thenReturn("테스트 리포트 내용");
        
        when(analysisReportRepository.findLatestReport(eq(userId), any(LocalDateTime.class)))
                .thenReturn(Optional.of(mockReport));

        // When
        AnalysisReport result = analysisService.findLatestReport(userId, periodEnd);

        // Then
        assertThat(result).isEqualTo(mockReport);
        assertThat(result.getAnalysisReportId()).isEqualTo("report123");
        assertThat(result.getContent()).isEqualTo("테스트 리포트 내용");
        verify(analysisReportRepository).findLatestReport(eq(userId), any(LocalDateTime.class));
    }

    @Test
    void findLatestReport_fail_no_report() {
        // Given
        String userId = "user123";
        LocalDate periodEnd = LocalDate.of(2024, 1, 31);
        
        when(analysisReportRepository.findLatestReport(eq(userId), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> analysisService.findLatestReport(userId, periodEnd));

        // Then
        assertThat(thrown)
                .isInstanceOf(RestApiException.class);
        assertThat(((RestApiException) thrown).getErrorCode())
                .isEqualTo(_NOT_FOUND.getCode());
    }
}