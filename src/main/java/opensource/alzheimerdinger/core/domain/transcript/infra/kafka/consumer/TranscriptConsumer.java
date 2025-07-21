package opensource.alzheimerdinger.core.domain.transcript.infra.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.request.TranscriptRequest;
import opensource.alzheimerdinger.core.domain.transcript.domain.service.TranscriptService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptConsumer {

    private final ObjectMapper objectMapper;
    private final TranscriptService transcriptService;

    @KafkaListener(
            topics = "${spring.kafka.topics.transcript}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(@Payload String message) {
        try {
            // JSON → DTO
            TranscriptRequest req = objectMapper.readValue(message, TranscriptRequest.class);

            // Service에서 저장
            transcriptService.save(req);

            // 로깅: session, conversation 개수, summary 개수
            log.info("Transcript saved: session={} convCount={} summaryCount={}",
                    req.getSessionId(),
                    req.getConversation() != null ? req.getConversation().size() : 0,
                    req.getSummary() != null ? req.getSummary().size() : 0);
        } catch (Exception e) {
            log.error("Failed to process transcript message: {}", message, e);
        }
    }
}