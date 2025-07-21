package opensource.alzheimerdinger.core.domain.batch.ui;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.batch.application.dto.request.TranscriptBatchRequest;
import opensource.alzheimerdinger.core.domain.batch.application.usecase.TranscriptBatchUseCase;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import static opensource.alzheimerdinger.core.global.exception.code.status.BatchErrorStatus.BATCH_INVALID_DATE_RANGE;

//Transcript 분석 요청을 위한 REST API 컨트롤러
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batch/")
public class TranscriptBatchController {

    private final TranscriptBatchUseCase transcriptBatchUseCase;

    //Transcript 분석 요청 - Kafka를 통한 즉시 전송
    @PostMapping("/transcript/analysis")
    public void requestTranscriptAnalysis(@RequestBody @Valid TranscriptBatchRequest request) {
        // 날짜 범위 검증 
        if (request.fromDate().isAfter(request.toDate())) {
            throw new RestApiException(BATCH_INVALID_DATE_RANGE);
        }
        
        transcriptBatchUseCase.sendTranscriptToKafka(request);
    }
} 