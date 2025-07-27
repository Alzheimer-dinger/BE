package opensource.alzheimerdinger.core.domain.analysis.domain.service;

import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisDayResponse;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.Analysis;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.AnalysisReport;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.AnalysisRepository;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.AnalysisReportRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    AnalysisRepository analysisRepository;

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
        
        Analysis analysis1 = mock(Analysis.class);
        Analysis analysis2 = mock(Analysis.class);
        List<Analysis> expectedAnalyses = List.of(analysis1, analysis2);
        
        when(analysisRepository.findByUserAndPeriod(userId, start, end)).thenReturn(expectedAnalyses);

        // When
        List<Analysis> result = analysisService.findAnalysisData(userId, start, end);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(analysis1, analysis2);
        verify(analysisRepository).findByUserAndPeriod(userId, start, end);
    }

    @Test
    void getPeriodData_success() {
        // Given
        String userId = "user123";
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);
        
        Analysis analysis1 = mock(Analysis.class);
        when(analysis1.getRiskScore()).thenReturn(0.2f);
        when(analysis1.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(analysis1.getHappy()).thenReturn(0.8f);
        when(analysis1.getSad()).thenReturn(0.1f);
        when(analysis1.getAngry()).thenReturn(0.05f);
        when(analysis1.getSurprised()).thenReturn(0.03f);
        when(analysis1.getBored()).thenReturn(0.02f);
        
        Analysis analysis2 = mock(Analysis.class);
        when(analysis2.getRiskScore()).thenReturn(0.4f);
        when(analysis2.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(analysis2.getHappy()).thenReturn(0.6f);
        when(analysis2.getSad()).thenReturn(0.3f);
        when(analysis2.getAngry()).thenReturn(0.06f);
        when(analysis2.getSurprised()).thenReturn(0.02f);
        when(analysis2.getBored()).thenReturn(0.02f);
        
        List<Analysis> analyses = List.of(analysis1, analysis2);
        
        when(analysisRepository.findByUserAndPeriod(userId, start, end)).thenReturn(analyses);

        // When
        AnalysisResponse result = analysisService.getPeriodData(userId, start, end);

        // Then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.start()).isEqualTo(start);
        assertThat(result.end()).isEqualTo(end);
        assertThat(result.averageRiskScore()).isEqualTo(0.3f); // (0.2 + 0.4) / 2
        assertThat(result.emotionTimeline()).hasSize(2);
        assertThat(result.totalParticipate()).isEqualTo(2);
    }

    @Test
    void getPeriodData_fail_no_data() {
        // Given
        String userId = "user123";
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);
        
        when(analysisRepository.findByUserAndPeriod(userId, start, end)).thenReturn(List.of());

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
        LocalDateTime date = LocalDateTime.of(2024, 1, 25, 14, 30);
        
        Analysis analysis1 = mock(Analysis.class);
        lenient().when(analysis1.getHappy()).thenReturn(0.7f);
        lenient().when(analysis1.getSad()).thenReturn(0.2f);
        lenient().when(analysis1.getAngry()).thenReturn(0.05f);
        lenient().when(analysis1.getSurprised()).thenReturn(0.03f);
        lenient().when(analysis1.getBored()).thenReturn(0.02f);
        lenient().when(analysis1.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 25, 10, 0));
        
        Analysis analysis2 = mock(Analysis.class);
        lenient().when(analysis2.getHappy()).thenReturn(0.8f);
        lenient().when(analysis2.getSad()).thenReturn(0.1f);
        lenient().when(analysis2.getAngry()).thenReturn(0.05f);
        lenient().when(analysis2.getSurprised()).thenReturn(0.03f);
        lenient().when(analysis2.getBored()).thenReturn(0.02f);
        lenient().when(analysis2.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 25, 15, 0));
        
        List<Analysis> dayAnalyses = List.of(analysis1, analysis2);
        
        // 모든 findByUserAndPeriod 호출에 대해 같은 결과 반환
        lenient().when(analysisRepository.findByUserAndPeriod(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(dayAnalyses);

        // When
        AnalysisDayResponse result = analysisService.getDayData(userId, date);

        // Then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.analysisDate()).isEqualTo(date);
        // 마지막 분석 데이터의 감정 점수들 (analysis2)
        assertThat(result.happyScore()).isEqualTo(0.8f);
        assertThat(result.sadScore()).isEqualTo(0.1f);
        assertThat(result.monthlyEmotionData()).isNotNull();
    }

    @Test
    void findLatestReport_success() {
        // Given
        String userId = "user123";
        LocalDateTime periodEnd = LocalDateTime.of(2024, 1, 31, 23, 59);
        
        AnalysisReport mockReport = mock(AnalysisReport.class);
        when(mockReport.getAnalysisReportId()).thenReturn("report123");
        when(mockReport.getReport()).thenReturn("테스트 리포트 내용");
        
        when(analysisReportRepository.findLatestReport(userId, periodEnd))
                .thenReturn(Optional.of(mockReport));

        // When
        AnalysisReport result = analysisService.findLatestReport(userId, periodEnd);

        // Then
        assertThat(result).isEqualTo(mockReport);
        assertThat(result.getAnalysisReportId()).isEqualTo("report123");
        assertThat(result.getReport()).isEqualTo("테스트 리포트 내용");
        verify(analysisReportRepository).findLatestReport(userId, periodEnd);
    }

    @Test
    void findLatestReport_fail_no_report() {
        // Given
        String userId = "user123";
        LocalDateTime periodEnd = LocalDateTime.of(2024, 1, 31, 23, 59);
        
        when(analysisReportRepository.findLatestReport(userId, periodEnd))
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