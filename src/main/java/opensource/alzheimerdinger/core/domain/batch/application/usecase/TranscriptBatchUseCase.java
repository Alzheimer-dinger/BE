package opensource.alzheimerdinger.core.domain.batch.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.application.dto.request.TranscriptBatchRequest;
import opensource.alzheimerdinger.core.domain.batch.application.dto.TranscriptDto;
import opensource.alzheimerdinger.core.domain.batch.domain.service.TranscriptBatchService;
import opensource.alzheimerdinger.core.domain.batch.infra.kafka.TranscriptProducer;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.stereotype.Service;

import java.util.List;

import static opensource.alzheimerdinger.core.global.exception.code.status.BatchErrorStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptBatchUseCase {

    private final TranscriptBatchService transcriptBatchService;
    private final TranscriptProducer transcriptProducer;


    //특정 유저의 특정 기간 Transcript를 Kafka로 직접 전송

    public void sendTranscriptToKafka(TranscriptBatchRequest request) {
        try {
            // 특정 유저의 특정 기간 Transcript 조회
            List<Transcript> transcripts = transcriptBatchService.findByUserIdAndPeriod(
                    request.userId(), request.fromDate(), request.toDate());
            
            if (transcripts.isEmpty()) {
                log.info("No transcripts found for user: {}, period: {} ~ {}", 
                        request.userId(), request.fromDate(), request.toDate());
                return;
            }
            
            log.info("Found {} transcripts for user: {}, period: {} ~ {}", 
                    transcripts.size(), request.userId(), request.fromDate(), request.toDate());
            
            // 각 Transcript를 DTO로 변환 후 Kafka로 전송
            for (Transcript transcript : transcripts) {
                TranscriptDto transcriptDto = convertToDto(transcript);
                transcriptProducer.sendTranscriptMessage(transcriptDto);
            }
            
            log.info("Successfully sent {} transcript messages to Kafka for user: {}", 
                    transcripts.size(), request.userId());
            
        } catch (Exception e) {
            log.error("Error sending transcript messages to Kafka for user: {}, period: {} ~ {}", 
                    request.userId(), request.fromDate(), request.toDate(), e);
            throw new RestApiException(BATCH_JOB_EXECUTION_FAILED);
        }
    }
    

    //Transcript Entity를 TranscriptDto로 변환

    private TranscriptDto convertToDto(Transcript transcript) {
        return new TranscriptDto(
                transcript.getTranscriptId(),
                transcript.getSessionId(),
                transcript.getUserId(),
                transcript.getStartTime(),
                transcript.getEndTime(),
                transcript.getConversation()
        );
    }
} 