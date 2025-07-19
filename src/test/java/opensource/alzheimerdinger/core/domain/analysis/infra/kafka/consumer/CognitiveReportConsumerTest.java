package opensource.alzheimerdinger.core.domain.analysis.infra.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensource.alzheimerdinger.core.domain.analysis.application.dto.request.CognitiveReportRequest;
import opensource.alzheimerdinger.core.domain.analysis.domain.entity.CognitiveReport;
import opensource.alzheimerdinger.core.domain.analysis.domain.repository.CognitiveReportRepository;
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
@EmbeddedKafka(
        partitions = 1,
        topics = "cognitive-report",
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092","port=9092"}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CognitiveReportConsumerTest {

    @Autowired KafkaTemplate<String, String> kafkaTemplate;
    @Autowired CognitiveReportRepository reportRepo;
    @Autowired ObjectMapper objectMapper;

    private static final String SESSION_ID = "session-cog-1";

    @BeforeEach
    void setUp() {
        reportRepo.deleteAll();
    }

    @Test
    void testConsumeCognitiveReportMessage() throws Exception {
        // Arrange
        var dto = new CognitiveReportRequest();
        dto.setReportId("report-456");
        dto.setSessionId(SESSION_ID);
        dto.setRiskScore(75f);
        dto.setRiskLabel(CognitiveReport.RiskLabel.RISK);
        dto.setCreatedAt(LocalDateTime.of(2025, 7, 11, 15, 30));

        String payload = objectMapper.writeValueAsString(dto);

        // Act
        kafkaTemplate.send(new ProducerRecord<>("cognitive-report", payload));
        kafkaTemplate.flush();
        Thread.sleep(500);

        // Assert
        List<CognitiveReport> saved = reportRepo.findBySessionId(SESSION_ID);
        assertThat(saved).hasSize(1);
        var cr = saved.get(0);
        assertThat(cr.getReportId()).isEqualTo("report-456");
        assertThat(cr.getRiskLabel()).isEqualTo(CognitiveReport.RiskLabel.RISK);
    }
}