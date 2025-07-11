package opensource.alzheimerdinger.core.domain.transcript.ui;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.request.TranscriptRequest;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptResponse;
import opensource.alzheimerdinger.core.domain.transcript.application.usecase.SaveTranscriptUseCase;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transcripts")
public class TranscriptController {
    private final SaveTranscriptUseCase saveUseCase;

    @PostMapping
    public ResponseEntity<BaseResponse<TranscriptResponse>> save(@Valid @RequestBody TranscriptRequest request) {
        TranscriptResponse resp = saveUseCase.execute(request);
        return ResponseEntity.ok(BaseResponse.onSuccess(resp));
    }
}
