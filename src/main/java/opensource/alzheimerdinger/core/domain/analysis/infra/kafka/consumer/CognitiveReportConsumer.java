package opensource.alzheimerdinger.core.domain.analysis.infra.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.CognitiveReportRequest;
import opensource.alzheimerdinger.core.domain.analysis.domain.service.CognitiveReportService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CognitiveReportConsumer {
    private final ObjectMapper objectMapper;
    private final CognitiveReportService reportService;

    @KafkaListener(
            topics = "${spring.kafka.topics.cognitive-report}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(@Payload String message) {
        try {
            CognitiveReportRequest dto = objectMapper.readValue(message, CognitiveReportRequest.class);
            reportService.saveCognitiveReport(dto);
            log.info("Saved cognitive-report for session {}", dto.getSessionId());
        } catch (Exception e) {
            log.error("Failed processing cognitive-report message", e);
        }
    }
}