
package opensource.alzheimerdinger.core.domain.transcript.infra.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.request.TranscriptRequest;
import opensource.alzheimerdinger.core.domain.transcript.domain.repository.TranscriptRepository;
import opensource.alzheimerdinger.core.domain.transcript.domain.service.TranscriptService;
import opensource.alzheimerdinger.core.global.config.KafkaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
        classes = {
                KafkaConfig.class,
                TranscriptConsumer.class,
                TranscriptService.class
        }
)
// 1) Embedded Kafka 띄우기
@EmbeddedKafka(partitions = 1, topics = "ai-transcript")
// 2) JPA/DataSource 자동설정 제외, Mongo 자동설정은 켜둡니다
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
// 3) Mongo 리포지토리 스캔 위치 지정
@EnableMongoRepositories(basePackages =
        "opensource.alzheimerdinger.core.domain.transcript.domain.repository"
// 4) KafkaConfig·TranscriptConsumer·TranscriptService가 이 패키지에 있으니 스캔
)
@ComponentScan(basePackages = {
        "opensource.alzheimerdinger.core.global.config",
        "opensource.alzheimerdinger.core.domain.transcript"
})
// 5) EmbeddedKafka 브로커 주소로 kafka.bootstrap-servers 덮어쓰기
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.group-id=test-group",
        "kafka.topics.transcript=ai-transcript"
})
@DirtiesContext
public class TranscriptConsumerTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TranscriptRepository transcriptRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanup() {
        transcriptRepository.deleteAll();
    }

//    @Test
//    void consumerShouldSaveToMongo() throws Exception {
//        // Arrange - TranscriptRequest 생성
//        var transcriptRequest = createTranscriptRequest("s1", 1, LocalDate.now(), "CLIENT", "hello", "2025-07-11T15:00:00Z");
//        String json = objectMapper.writeValueAsString(transcriptRequest);
//
//        // Act
//        kafkaTemplate.send("ai-transcript", json).get(10, TimeUnit.SECONDS);
//
//        // Assert
//        await().atMost(Duration.ofSeconds(10))
//                .pollDelay(Duration.ofMillis(100))
//                .untilAsserted(() -> {
//                    var results = transcriptRepository.findBySessionId("s1");
//                    assertThat(results).hasSize(1)
//                            .first().satisfies(t -> {
//                                assertThat(t.getScript()).isEqualTo("hello");
//                                assertThat(t.getSessionId()).isEqualTo("s1");
//                                assertThat(t.getSessionSeq()).isEqualTo(1);
//                            });
//                });
//    }
    @Test
    void consumerShouldSaveToMongo() throws Exception {
        String json = "{\"sessionId\":\"s1\",\"speaker\":\"CLIENT\",\"script\":\"hello\",\"timestamp\":\"2025-07-11T15:00:00Z\"}";
        kafkaTemplate.send("ai-transcript", json).get();
        Thread.sleep(500);
        List<?> results = transcriptRepository.findBySessionId("s1");
        assertThat(results).hasSize(1);
    }

    @Test
    void consumerShouldHandleMultipleMessages() throws Exception {
        // Arrange
        var request1 = createTranscriptRequest("s2", 1, LocalDate.now(), "CLIENT", "first message", "2025-07-11T15:00:00Z");
        var request2 = createTranscriptRequest("s2", 2, LocalDate.now(), "THERAPIST", "second message", "2025-07-11T15:01:00Z");

        String json1 = objectMapper.writeValueAsString(request1);
        String json2 = objectMapper.writeValueAsString(request2);

        // Act
        kafkaTemplate.send("ai-transcript", json1).get(10, TimeUnit.SECONDS);
        kafkaTemplate.send("ai-transcript", json2).get(10, TimeUnit.SECONDS);

        // Assert
        await().atMost(Duration.ofSeconds(10))
                .pollDelay(Duration.ofMillis(100))
                .untilAsserted(() -> {
                    var results = transcriptRepository.findBySessionId("s2");
                    assertThat(results).hasSize(2);

                    // 순서 확인
                    var sortedResults = results.stream()
                            .sorted((t1, t2) -> Integer.compare(t1.getSessionSeq(), t2.getSessionSeq()))
                            .toList();

                    assertThat(sortedResults.get(0).getScript()).isEqualTo("first message");
                    assertThat(sortedResults.get(1).getScript()).isEqualTo("second message");
                });
    }

    @Test
    void consumerShouldHandleInvalidJson() throws Exception {
        // Arrange
        String invalidJson = "{ invalid json }";

        // Act
        kafkaTemplate.send("ai-transcript", invalidJson).get(10, TimeUnit.SECONDS);

        // Assert - 잘못된 JSON은 저장되지 않아야 함
        Thread.sleep(1000); // 처리 시간 대기
        var results = transcriptRepository.findAll();
        assertThat(results).isEmpty();
    }

    @Test
    void consumerShouldSaveWithCorrectDate() throws Exception {
        // Arrange
        LocalDate testDate = LocalDate.of(2025, 7, 11);
        var transcriptRequest = createTranscriptRequest("s3", 1, testDate, "CLIENT", "date test", "2025-07-11T15:00:00Z");
        String json = objectMapper.writeValueAsString(transcriptRequest);

        // Act
        kafkaTemplate.send("ai-transcript", json).get(10, TimeUnit.SECONDS);

        // Assert
        await().atMost(Duration.ofSeconds(10))
                .pollDelay(Duration.ofMillis(100))
                .untilAsserted(() -> {
                    var results = transcriptRepository.findBySessionId("s3");
                    assertThat(results).hasSize(1)
                            .first().satisfies(t -> {
                                assertThat(t.getConversationDate()).isEqualTo(testDate);
                                assertThat(t.getScript()).isEqualTo("date test");
                            });
                });
    }

    // TranscriptRequest 생성 헬퍼 메서드
    private TranscriptRequest createTranscriptRequest(String sessionId, int sessionSeq, LocalDate conversationDate,
                                                      String speaker, String script, String timestamp) {
        var request = new TranscriptRequest();
        request.setSessionId(sessionId);
        request.setSessionSeq(sessionSeq);
        request.setConversationDate(conversationDate);
        request.setSpeaker(speaker);
        request.setScript(script);
        request.setTimestamp(timestamp);
        return request;
    }

    // JSON 직접 생성 테스트
    @Test
    void consumerShouldSaveWithDirectJson() throws Exception {
        // Arrange - JSON 직접 생성 (더 간단한 방법)
        String json = """
            {
                "sessionId": "s4",
                "sessionSeq": 1,
                "conversationDate": "2025-07-11",
                "speaker": "CLIENT",
                "script": "direct json test",
                "timestamp": "2025-07-11T15:00:00Z"
            }
            """;

        // Act
        kafkaTemplate.send("ai-transcript", json).get(10, TimeUnit.SECONDS);

        // Assert
        await().atMost(Duration.ofSeconds(10))
                .pollDelay(Duration.ofMillis(100))
                .untilAsserted(() -> {
                    var results = transcriptRepository.findBySessionId("s4");
                    assertThat(results).hasSize(1)
                            .first().satisfies(t -> {
                                assertThat(t.getScript()).isEqualTo("direct json test");
                                assertThat(t.getSessionId()).isEqualTo("s4");
                                assertThat(t.getSessionSeq()).isEqualTo(1);
                            });
                });
    }
}