package opensource.alzheimerdinger.core.domain.transcript.infra.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.request.TranscriptRequest;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.ConversationEntry;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.SummaryEntry;
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

@SpringBootTest(properties = {
        // Embedded Kafka 브로커 주소
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        // CORS 프로퍼티 (CorsProperties.prefix 가 "cors" 라고 가정)
        "cors.allowed-origins=*",
        "cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS",
        "cors.allowed-headers=*",
        "cors.allowed-exposed-headers=*",
        "cors.allow-credentials=true"
})
@EmbeddedKafka(partitions = 1, topics = "transcript")
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TranscriptConsumerTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

    @DynamicPropertySource
    static void setMongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired KafkaTemplate<String, String> kafkaTemplate;
    @Autowired TranscriptRepository transcriptRepository;
    @Autowired ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        transcriptRepository.deleteAll();
    }

    @Test
    void testConsumeTranscriptMessage() throws Exception {
        // Arrange
        var req = new TranscriptRequest();
        req.setSessionId("test-session");
        req.setUserId("user-1");
        req.setStartTime(LocalDateTime.of(2024, 7, 13, 0, 0));
        req.setEndTime(LocalDateTime.of(2024, 7, 13, 0, 5));

        req.setConversation(List.of(
                new ConversationEntry(ConversationEntry.Speaker.patient, "이것은 테스트 스크립트입니다.")
        ));

        // summary 데이터 추가
        LocalDateTime summaryTime = LocalDateTime.of(2024, 7, 13, 0, 3);
        req.setSummary(List.of(
                new SummaryEntry(summaryTime, "테스트 요약입니다.")
        ));

        String message = objectMapper.writeValueAsString(req);

        // Act
        kafkaTemplate.send(new ProducerRecord<>("transcript", message));
        kafkaTemplate.flush();
        Thread.sleep(1_000);  // 컨슈머가 처리할 시간

        // Assert
        List<Transcript> transcripts = transcriptRepository.findBySessionId("test-session");
        assertThat(transcripts).hasSize(1);

        var t = transcripts.get(0);

        // conversation 검증
        assertThat(t.getConversation()).hasSize(1)
                .allSatisfy(entry -> {
                    assertThat(entry.getSpeaker())
                            .isEqualTo(ConversationEntry.Speaker.patient);
                    assertThat(entry.getContent())
                            .isEqualTo("이것은 테스트 스크립트입니다.");
                });

        // summary 검증
        assertThat(t.getSummary()).hasSize(1)
                .allSatisfy(s -> {
                    assertThat(s.getTimestamp()).isEqualTo(summaryTime);
                    assertThat(s.getSummary()).isEqualTo("테스트 요약입니다.");
                });
    }
}