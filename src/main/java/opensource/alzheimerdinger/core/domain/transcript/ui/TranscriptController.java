package opensource.alzheimerdinger.core.domain.transcript.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptDetailResponse;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptListResponse;
import opensource.alzheimerdinger.core.domain.transcript.application.usecase.TranscriptUseCase;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Transcript", description = "통화 기록 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transcripts")
@SecurityRequirement(name = "Bearer Authentication")
public class TranscriptController {

	private final TranscriptUseCase transcriptUseCase;

	@Operation(
		summary = "통화 기록 목록 조회",
		description = "현재 사용자 기준 통화 기록 목록을 반환",
		responses = @ApiResponse(responseCode = "200", description = "조회 성공",
				content = @Content(schema = @Schema(implementation = TranscriptListResponse.class)))
	)
	@GetMapping
	public BaseResponse<List<TranscriptListResponse>> getTranscripts(
			@Parameter(hidden = true) @CurrentUser String userId
	) {
		return BaseResponse.onSuccess(transcriptUseCase.list(userId));
	}

	@Operation(
		summary = "통화 기록 상세 조회",
		description = "세션 ID로 통화 기록 상세를 조회",
		responses = @ApiResponse(responseCode = "200", description = "조회 성공",
				content = @Content(schema = @Schema(implementation = TranscriptDetailResponse.class)))
	)
	@GetMapping("/{sessionId}")
	public BaseResponse<TranscriptDetailResponse> getTranscriptDetail(
			@Parameter(hidden = true) @CurrentUser String userId,
			@PathVariable String sessionId
	) {
		return BaseResponse.onSuccess(transcriptUseCase.detail(userId, sessionId));
	}
} 