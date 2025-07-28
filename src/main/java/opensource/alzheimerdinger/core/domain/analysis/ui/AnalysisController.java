package opensource.alzheimerdinger.core.domain.analysis.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.AnalysisRequest;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.AnalysisReportRequest;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisDayResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.response.AnalysisReportResponse;
import opensource.alzheimerdinger.core.domain.analysis.application.usecase.AnalysisUseCase;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Analysis", description = "분석 리포트 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analysis")
public class AnalysisController {

    private final AnalysisUseCase analysisUseCase;

 
    //특정 기간 분석 데이터 조회(그래프 활용)  
    @Operation(
            summary = "기간별 감정 분석 조회",
            description = "지정된 기간의 감정 분석 데이터 및 그래프용 타임라인을 반환",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = AnalysisResponse.class))),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content)
            }
    )
    @PostMapping("/period")
    public BaseResponse<AnalysisResponse> getAnalysisByPeriod(
            @Valid @RequestBody AnalysisRequest request) {
        return BaseResponse.onSuccess(analysisUseCase.getAnalysisPeriodData(request));
    }

    
     //일별 감정 분석 데이터 조회 (달력용)     
    @Operation(
            summary = "일별 감정 분석 조회",
            description = "특정 날짜의 감정 분석 데이터와 달력 표시용 월간 데이터를 반환",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = AnalysisDayResponse.class))),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content)
            }
    )
    @GetMapping("/day")
    public BaseResponse<AnalysisDayResponse> getDayAnalysis(
            @CurrentUser String userId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return BaseResponse.onSuccess(analysisUseCase.getAnalysisDayData(userId, date));
    }

    
    //가장 최근 분석 리포트 조회
    @Operation(
            summary = "최근 분석 리포트 조회",
            description = "지정된 기간까지의 가장 최근 분석 리포트를 반환",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = AnalysisReportResponse.class))),
                    @ApiResponse(responseCode = "404", description = "리포트 없음", content = @Content)
            }
    )
    @PostMapping("/report/latest")
    public BaseResponse<AnalysisReportResponse> getLatestReport(
            @Valid @RequestBody AnalysisReportRequest request) {
        return BaseResponse.onSuccess(analysisUseCase.getLatestReport(request));
    }
}
