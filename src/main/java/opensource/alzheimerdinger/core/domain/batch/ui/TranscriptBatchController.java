package opensource.alzheimerdinger.core.domain.batch.ui;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.batch.application.dto.request.TranscriptBatchRequest;
import opensource.alzheimerdinger.core.domain.batch.application.dto.response.TranscriptBatchResponse;
import opensource.alzheimerdinger.core.domain.batch.application.usecase.TranscriptBatchUseCase;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

//Transcript 배치 실행을 위한 REST API 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batch/transcript")
public class TranscriptBatchController {

    private final TranscriptBatchUseCase transcriptBatchUseCase;

    //Transcript 배치 수동 실행
    @PostMapping("/execute")
    public BaseResponse<TranscriptBatchResponse> executeBatch(@RequestBody @Valid TranscriptBatchRequest request) {
        return BaseResponse.onSuccess(transcriptBatchUseCase.executeBatch(request));
    }
} 