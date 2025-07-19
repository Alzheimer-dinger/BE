package opensource.alzheimerdinger.core.domain.batch.infra.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.application.dto.TranscriptDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//Transcript 메시지를 Kafka로 전송하는 Producer
@Slf4j
@Component
@RequiredArgsConstructor
public class TranscriptProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // Kafka Topic 이름 하드코딩
    private static final String TRANSCRIPT_TOPIC = "request_transcript";

    //단일 Transcript 메시지 전송 (비동기)
    public void sendTranscriptMessage(TranscriptDto transcriptDto) {
        try {
            String message = objectMapper.writeValueAsString(transcriptDto);
            String key = transcriptDto.transcriptId();

            CompletableFuture<SendResult<String, String>> future = 
                kafkaTemplate.send(TRANSCRIPT_TOPIC, key, message);

            future.whenComplete((result, throwable) -> {
                if (throwable == null) {
                    log.info("Transcript message sent successfully: transcriptId={}, sessionId={}, conversationCount={}, offset={}", 
                            transcriptDto.transcriptId(), transcriptDto.sessionId(), 
                            transcriptDto.conversation().size(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send transcript message: transcriptId={}, sessionId={}, conversationCount={}", 
                            transcriptDto.transcriptId(), transcriptDto.sessionId(), 
                            transcriptDto.conversation().size(), throwable);
                }
            });

        } catch (Exception e) {
            log.error("Error converting transcript to JSON: {}", transcriptDto.transcriptId(), e);
            throw new RuntimeException("Failed to send transcript message", e);
        }
    }

    //Transcript 메시지 동기 전송 (배치에서 사용)
    public void sendTranscriptMessageSync(TranscriptDto transcriptDto) {
        try {
            String message = objectMapper.writeValueAsString(transcriptDto);
            String key = transcriptDto.transcriptId();

            SendResult<String, String> result = kafkaTemplate.send(TRANSCRIPT_TOPIC, key, message).get();
            
            log.info("Transcript message sent successfully: transcriptId={}, sessionId={}, conversationCount={}, offset={}", 
                    transcriptDto.transcriptId(), transcriptDto.sessionId(), 
                    transcriptDto.conversation().size(), result.getRecordMetadata().offset());

        } catch (ExecutionException e) {
            log.error("Failed to send transcript message (ExecutionException): transcriptId={}, sessionId={}, conversationCount={}", 
                    transcriptDto.transcriptId(), transcriptDto.sessionId(), 
                    transcriptDto.conversation().size(), e);
            throw new RuntimeException("Failed to send transcript message", e);
        } catch (InterruptedException e) {
            log.error("Kafka send interrupted for transcript: transcriptId={}, sessionId={}, conversationCount={}", 
                    transcriptDto.transcriptId(), transcriptDto.sessionId(), 
                    transcriptDto.conversation().size(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to send transcript message", e);
        } catch (Exception e) {
            log.error("Unexpected error in sync kafka send for transcript: {}", transcriptDto.transcriptId(), e);
            throw new RuntimeException("Failed to send transcript message", e);
        }
    }

    //토픽 이름 반환
    public String getTopicName() {
        return TRANSCRIPT_TOPIC;
    }
} 