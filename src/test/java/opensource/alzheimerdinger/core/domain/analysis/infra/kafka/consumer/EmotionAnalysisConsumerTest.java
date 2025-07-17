package opensource.alzheimerdinger.core.domain.analysis.infra.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.EmotionAnalysisRequest;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.ConversationSession;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.EmotionAnalysis;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.ConversationSessionRepository;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.EmotionAnalysisRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "emotion-analysis" }, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EmotionAnalysisConsumerTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    ConversationSessionRepository sessionRepo;
    @Autowired
    EmotionAnalysisRepository analysisRepo;
    @Autowired
    ObjectMapper objectMapper;

    final String SESSION_ID = "session-emo-1";

    @BeforeEach
    void setUp() {
        analysisRepo.deleteAll();
        sessionRepo.deleteAll();
        sessionRepo.save(ConversationSession.builder()
                .sessionId(SESSION_ID)
                .userId("user-emo")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .sessionSeq(1)
                .build()
        );
    }

    @Test
    void testConsumeEmotionAnalysisMessage() throws Exception {
        EmotionAnalysisRequest dto = new EmotionAnalysisRequest();
        dto.setAnalysisId("analysis-123");
        dto.setSessionId(SESSION_ID);
        dto.setAnalysisTime(LocalDateTime.of(2025,7,11,14,0));
        dto.setEmotionLabel("HAPPY");
        dto.setEmotionScore(0.92f);

        String payload = objectMapper.writeValueAsString(dto);
        kafkaTemplate.send(new ProducerRecord<>("emotion-analysis", payload));
        kafkaTemplate.flush();
        Thread.sleep(500);

        List<EmotionAnalysis> saved = analysisRepo.findBySession_SessionId(SESSION_ID);
        assertThat(saved).hasSize(1);
        var ea = saved.get(0);
        assertThat(ea.getAnalysisId()).isEqualTo("analysis-123");
    }
}