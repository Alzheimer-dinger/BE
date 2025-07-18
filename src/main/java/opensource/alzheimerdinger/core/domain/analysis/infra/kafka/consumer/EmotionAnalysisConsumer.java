package opensource.alzheimerdinger.core.domain.analysis.infra.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.EmotionAnalysisRequest;
import opensource.alzheimerdinger.core.domain.analysis.domain.service.EmotionAnalysisService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmotionAnalysisConsumer {
    private final ObjectMapper objectMapper;
    private final EmotionAnalysisService analysisService;

    @KafkaListener(
            topics = "${spring.kafka.topics.emotion-analysis}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(@Payload String message) {
        try {
            EmotionAnalysisRequest dto = objectMapper.readValue(message, EmotionAnalysisRequest.class);
            analysisService.saveEmotionAnalysis(dto);
            log.info("Saved emotion-analysis for session {}", dto.getSessionId());
        } catch (Exception e) {
            log.error("Failed processing emotion-analysis message", e);
        }
    }
}