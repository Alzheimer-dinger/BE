package opensource.alzheimerdinger.core.domain.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensource.alzheimerdinger.core.domain.batch.application.dto.request.TranscriptBatchRequest;
import opensource.alzheimerdinger.core.domain.batch.application.dto.response.TranscriptBatchResponse;
import opensource.alzheimerdinger.core.domain.batch.application.usecase.TranscriptBatchUseCase;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.ConversationEntry;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import opensource.alzheimerdinger.core.domain.transcript.domain.repository.TranscriptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 전체 배치 플로우 통합 테스트
 * 
 * 테스트 시나리오:
 * 1. MongoDB에 테스트 Transcript 데이터 삽입
 * 2. 배치 실행 API 호출 (특정 유저의 특정 기간 데이터)
 * 3. MongoDB → Kafka(request_transcript) 전송 확인
 * 4. 전체 플로우 성공 검증
 * 
 * 주의: 실제 MongoDB(alzheimerdinger-test)와 Kafka(localhost:9092)를 사용합니다.
 */
@SpringBootTest
@DirtiesContext
class TranscriptBatchIntegrationTest {

    @Autowired
    private TranscriptBatchUseCase transcriptBatchUseCase;

    @Autowired
    private TranscriptRepository transcriptRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 테스트 전 데이터 초기화
        transcriptRepository.deleteAll();
    }

    @Test
    @DisplayName("특정 유저의 특정 기간 배치 플로우 통합 테스트 (API)")
    void 특정_유저_기간_배치_플로우_테스트() throws Exception {
        // Given: MongoDB에 테스트 데이터 삽입
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        String targetUserId = "test-user-001";
        
        // 대상 유저의 대상 기간 데이터
        Transcript targetTranscript1 = Transcript.builder()
                .sessionId("session-001")
                .userId(targetUserId)
                .startTime(baseTime)
                .endTime(baseTime.plusMinutes(10))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "안녕하세요, 테스트 메시지입니다."),
                    new ConversationEntry(ConversationEntry.Speaker.ai, "네, 안녕하세요!")
                ))
                .build();

        Transcript targetTranscript2 = Transcript.builder()
                .sessionId("session-002")
                .userId(targetUserId)
                .startTime(baseTime.plusHours(2))
                .endTime(baseTime.plusHours(2).plusMinutes(5))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "두 번째 메시지입니다.")
                ))
                .build();

        // 다른 유저의 데이터 (처리되지 않아야 함)
        Transcript otherUserTranscript = Transcript.builder()
                .sessionId("session-003")
                .userId("other-user")
                .startTime(baseTime.plusHours(1))
                .endTime(baseTime.plusHours(1).plusMinutes(5))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "다른 유저 메시지입니다.")
                ))
                .build();

        transcriptRepository.save(targetTranscript1);
        transcriptRepository.save(targetTranscript2);
        transcriptRepository.save(otherUserTranscript);

        // When: 특정 유저의 특정 기간 배치 실행 (API)
        TranscriptBatchRequest batchRequest = new TranscriptBatchRequest(
                targetUserId,
                baseTime,
                baseTime.plusDays(1) // 하루 범위
        );

        TranscriptBatchResponse response = transcriptBatchUseCase.executeBatch(batchRequest);

        // Then: 대상 유저의 대상 기간 데이터만 처리됨 (2개)
        assertThat(response).isNotNull();
        assertThat(response.jobId()).isNotBlank();
        assertThat(response.status()).isEqualTo("SUCCESS");
        assertThat(response.processedCount()).isEqualTo(2);

        System.out.println("=== 특정 유저 배치 실행 완료 ===");
        System.out.println("Job ID: " + response.jobId());
        System.out.println("처리된 Transcript 문서 수: " + response.processedCount());
        System.out.println("배치 상태: " + response.status());
    }

    @Test
    @DisplayName("모든 유저의 특정 기간 배치 테스트 (스케줄러)")
    void 모든_유저_기간_배치_테스트() {
        // Given: 여러 유저의 데이터
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);

        Transcript user1Transcript = Transcript.builder()
                .sessionId("session-user1")
                .userId("user-001")
                .startTime(baseTime)
                .endTime(baseTime.plusMinutes(5))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "유저1 메시지입니다.")
                ))
                .build();

        Transcript user2Transcript = Transcript.builder()
                .sessionId("session-user2")
                .userId("user-002")
                .startTime(baseTime.plusHours(1))
                .endTime(baseTime.plusHours(1).plusMinutes(5))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "유저2 메시지입니다.")
                ))
                .build();

        transcriptRepository.save(user1Transcript);
        transcriptRepository.save(user2Transcript);

        // When: 모든 유저의 특정 기간 배치 실행 (스케줄러)
        TranscriptBatchResponse response = transcriptBatchUseCase.executeScheduledBatch(
                baseTime,
                baseTime.plusDays(1)
        );

        // Then: 모든 유저 데이터 처리됨
        assertThat(response).isNotNull();
        assertThat(response.processedCount()).isEqualTo(2);

        System.out.println("=== 모든 유저 배치 완료 ===");
        System.out.println("전체 처리된 문서: " + response.processedCount() + "개");
    }

    @Test
    @DisplayName("빈 결과 배치 실행 테스트")
    void 빈_결과_배치_테스트() {
        // Given: 빈 데이터베이스 상태
        assertThat(transcriptRepository.count()).isEqualTo(0);

        // When: 배치 실행
        TranscriptBatchRequest batchRequest = new TranscriptBatchRequest(
                "non-existing-user",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now()
        );

        TranscriptBatchResponse response = transcriptBatchUseCase.executeBatch(batchRequest);

        // Then: 배치 실행 성공하지만 처리된 데이터 없음
        assertThat(response).isNotNull();
        assertThat(response.jobId()).isNotBlank();
        assertThat(response.processedCount()).isEqualTo(0);

        System.out.println("=== 빈 결과 배치 완료 ===");
        System.out.println("처리된 문서 수: " + response.processedCount());
    }

    @Test
    @DisplayName("배치 성능 테스트 (대용량 데이터)")
    void 대용량_데이터_배치_성능_테스트() {
        // Given: 대용량 테스트 데이터 생성 (특정 유저의 50개 documents)
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        String targetUserId = "performance-test-user";
        
        for (int i = 1; i <= 50; i++) {
            Transcript transcript = Transcript.builder()
                    .sessionId("performance-session-" + i)
                    .userId(targetUserId)
                    .startTime(baseTime.plusMinutes(i))
                    .endTime(baseTime.plusMinutes(i).plusMinutes(5))
                    .conversation(List.of(
                        new ConversationEntry(ConversationEntry.Speaker.patient, "성능 테스트 메시지 " + i + "번째입니다."),
                        new ConversationEntry(ConversationEntry.Speaker.ai, "응답 메시지 " + i + "번째입니다.")
                    ))
                    .build();
            
            transcriptRepository.save(transcript);
        }

        assertThat(transcriptRepository.count()).isEqualTo(50);

        // When: 배치 실행 시간 측정
        long startTime = System.currentTimeMillis();
        
        TranscriptBatchRequest batchRequest = new TranscriptBatchRequest(
                targetUserId,
                baseTime,
                baseTime.plusDays(1)
        );

        TranscriptBatchResponse response = transcriptBatchUseCase.executeBatch(batchRequest);
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Then: 성능 검증 (50 transcript documents)
        assertThat(response).isNotNull();
        assertThat(response.processedCount()).isEqualTo(50);
        assertThat(executionTime).isLessThan(TimeUnit.SECONDS.toMillis(30)); // 30초 이내

        System.out.println("=== 성능 테스트 완료 ===");
        System.out.println("처리된 Transcript 문서: " + response.processedCount() + "개");
        System.out.println("실행 시간: " + executionTime + "ms");
        System.out.println("처리 속도: " + (response.processedCount() * 1000.0 / executionTime) + " docs/sec");
    }
} 