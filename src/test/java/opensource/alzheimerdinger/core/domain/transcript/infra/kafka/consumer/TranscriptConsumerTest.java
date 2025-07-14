package opensource.alzheimerdinger.core.domain.transcript.infra.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.request.TranscriptRequest;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import opensource.alzheimerdinger.core.domain.transcript.domain.repository.TranscriptRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = { "ai-transcript" }, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TranscriptConsumerTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

    @DynamicPropertySource
    static void setMongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TranscriptRepository transcriptRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        transcriptRepository.deleteAll();
    }

    @Test
    void testConsumeTranscriptMessage() throws Exception {
        // 예시 데이터 생성
        TranscriptRequest request = new TranscriptRequest();
        request.setSessionId("test-session");
        request.setSessionSeq(1);
        request.setConversationDate(LocalDateTime.of(2024, 7, 13, 0, 0));
        request.setSpeaker("CLIENT");
        request.setScript("이것은 테스트 스크립트입니다.");

        // JSON 직렬화
        String message = objectMapper.writeValueAsString(request);

        // Kafka로 메시지 발행
        kafkaTemplate.send(new ProducerRecord<>("ai-transcript", message));
        kafkaTemplate.flush();

        // 메시지 소비 및 저장까지 약간의 대기 필요
        Thread.sleep(2000);

        // 저장된 데이터 검증
        List<Transcript> transcripts = transcriptRepository.findBySessionId("test-session");
        assertThat(transcripts).hasSize(1);
        Transcript transcript = transcripts.get(0);
        assertThat(transcript.getScript()).isEqualTo("이것은 테스트 스크립트입니다.");
        assertThat(transcript.getSpeaker().name()).isEqualTo("CLIENT");
    }
}