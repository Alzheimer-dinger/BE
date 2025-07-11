package opensource.alzheimerdinger.core.domain.transcript.infra.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.request.TranscriptRequest;
import opensource.alzheimerdinger.core.domain.transcript.application.usecase.SaveTranscriptUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptConsumer {
    private final ObjectMapper objectMapper;
    private final SaveTranscriptUseCase saveUseCase;

    @KafkaListener(
            topics = "${kafka.topics.transcript}",
            groupId = "${kafka.consumer.group-id}"
    )
    public void listen(@Payload String message) {
        try {
            TranscriptRequest req = objectMapper.readValue(message, TranscriptRequest.class);
            saveUseCase.execute(req);
            log.info("Transcript saved: session={}, speaker={}", req.getSessionId(), req.getSpeaker());
        } catch (Exception e) {
            log.error("Failed to process transcript message: {}", message, e);
            // Retry 로직 짜야할지도
        }
    }
}