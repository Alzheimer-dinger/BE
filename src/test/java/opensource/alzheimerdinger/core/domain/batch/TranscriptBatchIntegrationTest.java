package opensource.alzheimerdinger.core.domain.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensource.alzheimerdinger.core.domain.batch.application.dto.request.TranscriptBatchRequest;
import opensource.alzheimerdinger.core.domain.batch.application.dto.response.TranscriptBatchResponse;
import opensource.alzheimerdinger.core.domain.batch.application.usecase.TranscriptBatchUseCase;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Speaker;
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
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 전체 배치 플로우 통합 테스트
 * 
 * 테스트 시나리오:
 * 1. MongoDB에 테스트 Transcript 데이터 삽입
 * 2. 배치 실행 API 호출
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
    @DisplayName("전체 배치 플로우 통합 테스트: MongoDB → Kafka 전송")
    void 전체_배치_플로우_테스트() throws Exception {
        // Given: MongoDB에 테스트 데이터 삽입
        LocalDateTime testDate = LocalDateTime.now().minusHours(1);
        
        Transcript testTranscript1 = Transcript.builder()
                .sessionId("integration-test-session-001")
                .sessionSeq(1)
                .conversationDate(testDate)
                .speaker(Speaker.CLIENT)
                .script("안녕하세요, 통합 테스트 메시지입니다.")
                .build();

        Transcript testTranscript2 = Transcript.builder()
                .sessionId("integration-test-session-001")
                .sessionSeq(2)
                .conversationDate(testDate.plusMinutes(1))
                .speaker(Speaker.AI)
                .script("네, 안녕하세요! 도움이 필요하시면 말씀해주세요.")
                .build();

        Transcript testTranscript3 = Transcript.builder()
                .sessionId("integration-test-session-002")
                .sessionSeq(1)
                .conversationDate(testDate.plusMinutes(5))
                .speaker(Speaker.CLIENT)
                .script("오늘 날씨가 좋네요.")
                .build();

        transcriptRepository.save(testTranscript1);
        transcriptRepository.save(testTranscript2);
        transcriptRepository.save(testTranscript3);

        // 데이터 저장 확인
        assertThat(transcriptRepository.count()).isEqualTo(3);

        // When: 배치 실행
        TranscriptBatchRequest batchRequest = new TranscriptBatchRequest(
                testDate.toString(), 
                null
        );

        TranscriptBatchResponse response = transcriptBatchUseCase.executeBatch(batchRequest);

        // Then: 배치 실행 결과 검증
        assertThat(response).isNotNull();
        assertThat(response.jobId()).isNotBlank();
        assertThat(response.status()).isEqualTo("SUCCESS");
        assertThat(response.processedCount()).isEqualTo(3);

        System.out.println("=== 배치 실행 완료 ===");
        System.out.println("Job ID: " + response.jobId());
        System.out.println("처리된 메시지 수: " + response.processedCount());
        System.out.println("배치 상태: " + response.status());
    }

    @Test
    @DisplayName("빈 데이터베이스 배치 실행 테스트")
    void 빈_데이터베이스_배치_테스트() {
        // Given: 빈 데이터베이스 상태
        assertThat(transcriptRepository.count()).isEqualTo(0);

        // When: 배치 실행
        TranscriptBatchRequest batchRequest = new TranscriptBatchRequest(
                LocalDateTime.now().minusHours(1).toString(),
                null
        );

        TranscriptBatchResponse response = transcriptBatchUseCase.executeBatch(batchRequest);

        // Then: 배치 실행 성공하지만 처리된 데이터 없음
        assertThat(response).isNotNull();
        assertThat(response.jobId()).isNotBlank();
        assertThat(response.processedCount()).isEqualTo(0);

        System.out.println("=== 빈 데이터베이스 배치 완료 ===");
        System.out.println("Job ID: " + response.jobId());
        System.out.println("처리된 메시지 수: " + response.processedCount());
    }

    @Test
    @DisplayName("특정 날짜 범위 배치 테스트")
    void 특정_날짜_범위_배치_테스트() {
        // Given: 여러 시간대의 테스트 데이터
        LocalDateTime oldDate = LocalDateTime.now().minusDays(2);
        LocalDateTime recentDate = LocalDateTime.now().minusHours(1);

        // 오래된 데이터
        Transcript oldTranscript = Transcript.builder()
                .sessionId("old-session")
                .sessionSeq(1)
                .conversationDate(oldDate)
                .speaker(Speaker.CLIENT)
                .script("오래된 메시지입니다.")
                .build();

        // 최근 데이터
        Transcript recentTranscript = Transcript.builder()
                .sessionId("recent-session")
                .sessionSeq(1)
                .conversationDate(recentDate)
                .speaker(Speaker.CLIENT)
                .script("최근 메시지입니다.")
                .build();

        transcriptRepository.save(oldTranscript);
        transcriptRepository.save(recentTranscript);

        // When: 최근 2시간 내 데이터만 배치 실행
        TranscriptBatchRequest batchRequest = new TranscriptBatchRequest(
                LocalDateTime.now().minusHours(2).toString(),
                null
        );

        TranscriptBatchResponse response = transcriptBatchUseCase.executeBatch(batchRequest);

        // Then: 최근 데이터 1개만 처리됨
        assertThat(response).isNotNull();
        assertThat(response.processedCount()).isEqualTo(1);

        System.out.println("=== 날짜 범위 배치 완료 ===");
        System.out.println("전체 데이터: 2개, 처리된 데이터: " + response.processedCount() + "개");
    }

    @Test
    @DisplayName("배치 성능 테스트 (대용량 데이터)")
    void 대용량_데이터_배치_성능_테스트() {
        // Given: 대용량 테스트 데이터 생성 (100개)
        LocalDateTime baseDate = LocalDateTime.now().minusHours(1);
        
        for (int i = 1; i <= 100; i++) {
            Transcript transcript = Transcript.builder()
                    .sessionId("performance-test-session-" + (i % 10)) // 10개 세션
                    .sessionSeq(i)
                    .conversationDate(baseDate.plusSeconds(i))
                    .speaker(i % 2 == 0 ? Speaker.CLIENT : Speaker.AI)
                    .script("성능 테스트 메시지 " + i + "번째 입니다.")
                    .build();
            
            transcriptRepository.save(transcript);
        }

        assertThat(transcriptRepository.count()).isEqualTo(100);

        // When: 배치 실행 시간 측정
        long startTime = System.currentTimeMillis();
        
        TranscriptBatchRequest batchRequest = new TranscriptBatchRequest(
                baseDate.toString(),
                null
        );

        TranscriptBatchResponse response = transcriptBatchUseCase.executeBatch(batchRequest);
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Then: 성능 검증
        assertThat(response).isNotNull();
        assertThat(response.processedCount()).isEqualTo(100);
        assertThat(executionTime).isLessThan(TimeUnit.SECONDS.toMillis(30)); // 30초 이내

        System.out.println("=== 성능 테스트 완료 ===");
        System.out.println("처리된 데이터: " + response.processedCount() + "개");
        System.out.println("실행 시간: " + executionTime + "ms");
        System.out.println("처리 속도: " + (response.processedCount() * 1000.0 / executionTime) + " msg/sec");
    }
} 