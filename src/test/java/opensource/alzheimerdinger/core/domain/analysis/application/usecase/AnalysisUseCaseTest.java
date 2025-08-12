package opensource.alzheimerdinger.core.domain.analysis.application.usecase;

import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisDayResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisReportResponse;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.AnalysisReport;
import opensource.alzheimerdinger.core.domain.analysis.domain.service.AnalysisService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisUseCaseTest {

    @Mock
    AnalysisService analysisService;

    @InjectMocks
    AnalysisUseCase analysisUseCase;

    @Test
    void getAnalysisPeriodData_success() {
        // Given
        String userId = "user123";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        
        AnalysisResponse expectedResponse = new AnalysisResponse(
                userId, start, end, 0.3,
                List.of(), 5, "10분 30초"
        );
        
        when(analysisService.getPeriodData(userId, start, end)).thenReturn(expectedResponse);

        // When
        AnalysisResponse result = analysisUseCase.getAnalysisPeriodData(userId, start, end);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.averageRiskScore()).isEqualTo(0.3);
        verify(analysisService).getPeriodData(userId, start, end);
    }

    @Test
    void getAnalysisPeriodData_fail_no_data() {
        // Given
        String userId = "user123";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 31);
        
        when(analysisService.getPeriodData(userId, start, end))
                .thenThrow(new RestApiException(_NOT_FOUND));

        // When
        Throwable thrown = catchThrowable(() -> analysisUseCase.getAnalysisPeriodData(userId, start, end));

        // Then
        assertThat(thrown)
                .isInstanceOf(RestApiException.class);
        assertThat(((RestApiException) thrown).getErrorCode())
                .isEqualTo(_NOT_FOUND.getCode());
    }

    @Test
    void getAnalysisDayData_success() {
        // Given
        String userId = "user123";
        LocalDate date = LocalDate.of(2024, 1, 25);
        
        AnalysisDayResponse expectedResponse = new AnalysisDayResponse(
                userId, date, 0.8, 0.1, 0.05, 0.03, 0.02, List.of()
        );
        
        when(analysisService.getDayData(userId, date)).thenReturn(expectedResponse);

        // When
        AnalysisDayResponse result = analysisUseCase.getAnalysisDayData(userId, date);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.happyScore()).isEqualTo(0.8);
        verify(analysisService).getDayData(userId, date);
    }

    @Test
    void getAnalysisData_fail_no_Day_data() {
        // Given
        String userId = "user123";
        LocalDate date = LocalDate.of(2024, 1, 25);
        
        when(analysisService.getDayData(userId, date))
                .thenThrow(new RestApiException(_NOT_FOUND));

        // When
        Throwable thrown = catchThrowable(() -> analysisUseCase.getAnalysisDayData(userId, date));

        // Then
        assertThat(thrown)
                .isInstanceOf(RestApiException.class);
        assertThat(((RestApiException) thrown).getErrorCode())
                .isEqualTo(_NOT_FOUND.getCode());
    }

    @Test
    void getLatestReport_success() {
        // Given
        String userId = "user123";
        LocalDate periodEnd = LocalDate.of(2024, 1, 31);
        
        AnalysisReport mockReport = mock(AnalysisReport.class);
        when(mockReport.getAnalysisReportId()).thenReturn("report123");
        when(mockReport.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 30, 15, 0));
        when(mockReport.getContent()).thenReturn("1월 종합 분석 결과입니다.");
        
        when(analysisService.findLatestReport(userId, periodEnd)).thenReturn(mockReport);

        // When
        AnalysisReportResponse result = analysisUseCase.getLatestReport(userId, periodEnd);

        // Then
        assertThat(result.reportId()).isEqualTo("report123");
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.report()).isEqualTo("1월 종합 분석 결과입니다.");
        verify(analysisService).findLatestReport(userId, periodEnd);
    }

    @Test
    void getLatestReport_fail_no_report() {
        // Given
        String userId = "user123";
        LocalDate periodEnd = LocalDate.of(2024, 1, 31);
        
        when(analysisService.findLatestReport(userId, periodEnd))
                .thenThrow(new RestApiException(_NOT_FOUND));

        // When
        Throwable thrown = catchThrowable(() -> analysisUseCase.getLatestReport(userId, periodEnd));

        // Then
        assertThat(thrown)
                .isInstanceOf(RestApiException.class);
        assertThat(((RestApiException) thrown).getErrorCode())
                .isEqualTo(_NOT_FOUND.getCode());
    }

    @Test
    void getLatestReport_with_valid_data_structure() {
        // Given
        String userId = "user123";
        LocalDate periodEnd = LocalDate.of(2024, 1, 31);
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 30, 15, 0);
        
        AnalysisReport mockReport = mock(AnalysisReport.class);
        when(mockReport.getAnalysisReportId()).thenReturn("report123");
        when(mockReport.getCreatedAt()).thenReturn(createdAt);
        when(mockReport.getContent()).thenReturn("상세 분석 리포트 내용");
        
        when(analysisService.findLatestReport(userId, periodEnd)).thenReturn(mockReport);

        // When
        AnalysisReportResponse result = analysisUseCase.getLatestReport(userId, periodEnd);

        // Then
        assertThat(result.reportId()).isEqualTo("report123");
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.createdAt()).isEqualTo(createdAt.toLocalDate());
        assertThat(result.report()).isEqualTo("상세 분석 리포트 내용");
        
        // AnalysisReportResponse의 모든 필드가 올바르게 매핑되었는지 확인
        assertThat(result).isNotNull();
        assertThat(result.reportId()).isNotBlank();
        assertThat(result.userId()).isNotBlank();
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.report()).isNotBlank();
    }
} 