package opensource.alzheimerdinger.core.domain.analysis.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
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
            description = "지정된 기간의 감정 분석 데이터 및 그래프용 타임라인을 반환합니다. 쿼리 파라미터로 시작일과 종료일을 전달하여 해당 기간의 분석 결과를 조회할 수 있습니다.",
            parameters = {
                    @Parameter(name = "start", description = "조회 시작일 (YYYY-MM-DD)", required = true, example = "2024-01-01"),
                    @Parameter(name = "end", description = "조회 종료일 (YYYY-MM-DD)", required = true, example = "2024-01-31")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = AnalysisResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = @Content),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content)
            }
    )
    @GetMapping("/period")
    public BaseResponse<AnalysisResponse> getAnalysisByPeriod(
            @CurrentUser String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return BaseResponse.onSuccess(analysisUseCase.getAnalysisPeriodData(userId, start, end));
    }

    
     //일별 감정 분석 데이터 조회 (달력용)     
    @Operation(
            summary = "일별 감정 분석 조회",
            description = "특정 날짜의 감정 분석 데이터와 달력 표시용 월간 데이터를 반환합니다.",
            parameters = {
                    @Parameter(name = "date", description = "조회할 날짜 (YYYY-MM-DD)", required = true, example = "2024-01-15")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = AnalysisDayResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 날짜 형식", content = @Content),
                    @ApiResponse(responseCode = "404", description = "데이터 없음", content = @Content)
            }
    )
    @GetMapping("/day")
    public BaseResponse<AnalysisDayResponse> getDayAnalysis(
            @CurrentUser String userId, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return BaseResponse.onSuccess(analysisUseCase.getAnalysisDayData(userId, date));
    }

    
    //가장 최근 분석 리포트 조회
    @Operation(
            summary = "최근 분석 리포트 조회",
            description = "지정된 기간까지의 가장 최근 분석 리포트를 반환합니다. 기간 종료일을 기준으로 해당 날짜 이전에 생성된 가장 최신 리포트를 조회합니다.",
            parameters = {
                    @Parameter(name = "periodEnd", description = "리포트 조회 기준 종료일 (YYYY-MM-DD)", required = true, example = "2024-01-31")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = AnalysisReportResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 날짜 형식", content = @Content),
                    @ApiResponse(responseCode = "404", description = "리포트 없음", content = @Content)
            }
    )
    @GetMapping("/report/latest")
    public BaseResponse<AnalysisReportResponse> getLatestReport(
            @CurrentUser String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        return BaseResponse.onSuccess(analysisUseCase.getLatestReport(userId, periodEnd));
    }
}
