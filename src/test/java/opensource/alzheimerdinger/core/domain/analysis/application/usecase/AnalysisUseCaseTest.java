package opensource.alzheimerdinger.core.domain.analysis.application.usecase;

import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.AnalysisRequest;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.AnalysisReportRequest;
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
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);
        AnalysisRequest request = new AnalysisRequest("user123", start, end);
        
        AnalysisResponse expectedResponse = new AnalysisResponse(
                "user123", start, end, 0.3f,
                List.of(), 5, "10분 30초"
        );
        
        when(analysisService.getPeriodData("user123", start, end)).thenReturn(expectedResponse);

        // When
        AnalysisResponse result = analysisUseCase.getAnalysisPeriodData(request);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.averageRiskScore()).isEqualTo(0.3f);
        verify(analysisService).getPeriodData("user123", start, end);
    }

    @Test
    void getAnalysisPeriodData_fail_no_data() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);
        AnalysisRequest request = new AnalysisRequest("user123", start, end);
        
        when(analysisService.getPeriodData("user123", start, end))
                .thenThrow(new RestApiException(_NOT_FOUND));

        // When
        Throwable thrown = catchThrowable(() -> analysisUseCase.getAnalysisPeriodData(request));

        // Then
        assertThat(thrown)
                .isInstanceOf(RestApiException.class);
        assertThat(((RestApiException) thrown).getErrorCode())
                .isEqualTo(_NOT_FOUND.getCode());
    }

    @Test
    void getDayAnalysisData_success() {
        // Given
        String userId = "user123";
        LocalDateTime date = LocalDateTime.of(2024, 1, 25, 14, 30);
        
        AnalysisDayResponse expectedResponse = new AnalysisDayResponse(
                userId, date, 0.8f, 0.1f, 0.05f, 0.03f, 0.02f, List.of()
        );
        
        when(analysisService.getDayData(userId, date)).thenReturn(expectedResponse);

        // When
        AnalysisDayResponse result = analysisUseCase.getDayAnalysisData(userId, date);

        // Then
        assertThat(result).isEqualTo(expectedResponse);
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.happyScore()).isEqualTo(0.8f);
        verify(analysisService).getDayData(userId, date);
    }

    @Test
    void getDayAnalysisData_fail_no_data() {
        // Given
        String userId = "user123";
        LocalDateTime date = LocalDateTime.of(2024, 1, 25, 14, 30);
        
        when(analysisService.getDayData(userId, date))
                .thenThrow(new RestApiException(_NOT_FOUND));

        // When
        Throwable thrown = catchThrowable(() -> analysisUseCase.getDayAnalysisData(userId, date));

        // Then
        assertThat(thrown)
                .isInstanceOf(RestApiException.class);
        assertThat(((RestApiException) thrown).getErrorCode())
                .isEqualTo(_NOT_FOUND.getCode());
    }

    @Test
    void getLatestReport_success() {
        // Given
        LocalDateTime periodEnd = LocalDateTime.of(2024, 1, 31, 23, 59);
        AnalysisReportRequest request = new AnalysisReportRequest("user123", periodEnd);
        
        AnalysisReport mockReport = mock(AnalysisReport.class);
        when(mockReport.getAnalysisReportId()).thenReturn("report123");
        when(mockReport.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 30, 15, 0));
        when(mockReport.getReport()).thenReturn("1월 종합 분석 결과입니다.");
        
        when(analysisService.findLatestReport("user123", periodEnd)).thenReturn(mockReport);

        // When
        AnalysisReportResponse result = analysisUseCase.getLatestReport(request);

        // Then
        assertThat(result.reportId()).isEqualTo("report123");
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.report()).isEqualTo("1월 종합 분석 결과입니다.");
        verify(analysisService).findLatestReport("user123", periodEnd);
    }

    @Test
    void getLatestReport_fail_no_report() {
        // Given
        LocalDateTime periodEnd = LocalDateTime.of(2024, 1, 31, 23, 59);
        AnalysisReportRequest request = new AnalysisReportRequest("user123", periodEnd);
        
        when(analysisService.findLatestReport("user123", periodEnd))
                .thenThrow(new RestApiException(_NOT_FOUND));

        // When
        Throwable thrown = catchThrowable(() -> analysisUseCase.getLatestReport(request));

        // Then
        assertThat(thrown)
                .isInstanceOf(RestApiException.class);
        assertThat(((RestApiException) thrown).getErrorCode())
                .isEqualTo(_NOT_FOUND.getCode());
    }

    @Test
    void getLatestReport_with_valid_data_structure() {
        // Given
        LocalDateTime periodEnd = LocalDateTime.of(2024, 1, 31, 23, 59);
        AnalysisReportRequest request = new AnalysisReportRequest("user123", periodEnd);
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 30, 15, 0);
        
        AnalysisReport mockReport = mock(AnalysisReport.class);
        when(mockReport.getAnalysisReportId()).thenReturn("report123");
        when(mockReport.getCreatedAt()).thenReturn(createdAt);
        when(mockReport.getReport()).thenReturn("상세 분석 리포트 내용");
        
        when(analysisService.findLatestReport("user123", periodEnd)).thenReturn(mockReport);

        // When
        AnalysisReportResponse result = analysisUseCase.getLatestReport(request);

        // Then
        assertThat(result.reportId()).isEqualTo("report123");
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.createdAt()).isEqualTo(createdAt);
        assertThat(result.report()).isEqualTo("상세 분석 리포트 내용");
        
        // AnalysisReportResponse의 모든 필드가 올바르게 매핑되었는지 확인
        assertThat(result).isNotNull();
        assertThat(result.reportId()).isNotBlank();
        assertThat(result.userId()).isNotBlank();
        assertThat(result.createdAt()).isNotNull();
        assertThat(result.report()).isNotBlank();
    }
} 