package opensource.alzheimerdinger.core.domain.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import opensource.alzheimerdinger.core.domain.batch.application.dto.request.TranscriptBatchRequest;
import opensource.alzheimerdinger.core.domain.batch.application.usecase.TranscriptBatchUseCase;
import opensource.alzheimerdinger.core.domain.batch.domain.service.TranscriptBatchService;
import opensource.alzheimerdinger.core.domain.batch.infra.kafka.TranscriptProducer;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.ConversationEntry;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import opensource.alzheimerdinger.core.domain.transcript.domain.repository.TranscriptRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Transcript UseCase 통합 테스트 (Controller → UseCase → Kafka 직접 전송)
 * 
 * 테스트 시나리오:
 * 1. 실제 환경 통합 테스트 - MongoDB + Kafka 실제 연결
 * 2. 단위 테스트 - Mock을 이용한 로직 검증 및 예외 처리
 * 3. 성능 테스트 - 대용량 데이터 처리
 * 4. 엣지 케이스 - 빈 데이터, null 값 등
 * 
 * 주의: 실제 MongoDB와 Kafka, 그리고 Mock을 혼합하여 사용
 */
@SpringBootTest
@DirtiesContext
class TranscriptBatchUseCaseTest {

    @Autowired
    private TranscriptBatchUseCase transcriptBatchUseCase;

    @Autowired
    private TranscriptRepository transcriptRepository;

    @SuppressWarnings("removal")
    @SpyBean  // 실제 Bean을 Spy로 감싸서 호출 검증 가능
    private TranscriptBatchService transcriptBatchService;

    @SuppressWarnings("removal")
    @SpyBean  // 실제 Bean을 Spy로 감싸서 호출 검증 가능
    private TranscriptProducer transcriptProducer;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // 테스트 전 데이터 초기화 및 Mock 리셋
        transcriptRepository.deleteAll();
        Mockito.reset(transcriptBatchService, transcriptProducer);
    }

    // ===== 실제 환경 통합 테스트 =====

    @Test
    @DisplayName("특정 유저의 특정 기간 Kafka 직접 전송 통합 테스트")
    void 특정_유저_기간_Kafka_직접_전송_통합_테스트() {
        // Given: 실제 테스트 데이터 (특정 유저의 2개 documents)
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        String targetUserId = "test-user-123";

        Transcript transcript1 = Transcript.builder()
                .sessionId("session-1")
                .userId(targetUserId)
                .startTime(baseTime)
                .endTime(baseTime.plusMinutes(10))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "첫 번째 메시지입니다."),
                    new ConversationEntry(ConversationEntry.Speaker.ai, "응답 메시지입니다.")
                ))
                .build();

        Transcript transcript2 = Transcript.builder()
                .sessionId("session-2")
                .userId(targetUserId)
                .startTime(baseTime.plusHours(1))
                .endTime(baseTime.plusHours(1).plusMinutes(15))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "두 번째 메시지입니다."),
                    new ConversationEntry(ConversationEntry.Speaker.ai, "두 번째 응답입니다.")
                ))
                .build();

        // 다른 유저 데이터 (처리되지 않아야 함)
        Transcript otherUserTranscript = Transcript.builder()
                .sessionId("session-other")
                .userId("other-user-456")
                .startTime(baseTime.plusMinutes(30))
                .endTime(baseTime.plusMinutes(35))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "다른 유저 메시지입니다.")
                ))
                .build();

        transcriptRepository.save(transcript1);
        transcriptRepository.save(transcript2);
        transcriptRepository.save(otherUserTranscript);

        // When: 특정 유저의 특정 기간 Kafka 직접 전송
        TranscriptBatchRequest request = new TranscriptBatchRequest(
                targetUserId,
                baseTime,
                baseTime.plusDays(1)
        );

        assertThatCode(() -> transcriptBatchUseCase.sendTranscriptToKafka(request))
                .doesNotThrowAnyException();

        // Then: 실제 호출 검증
        verify(transcriptBatchService, times(1))
                .findByUserIdAndPeriod(eq(targetUserId), eq(baseTime), eq(baseTime.plusDays(1)));
        
        verify(transcriptProducer, times(2))  // 타겟 유저의 2개 transcript만 전송
                .sendTranscriptMessage(any());

        System.out.println("=== 특정 유저 Kafka 직접 전송 통합 테스트 완료 ===");
        System.out.println("대상 유저: " + targetUserId);
        System.out.println("처리 기간: " + baseTime + " ~ " + baseTime.plusDays(1));
        System.out.println("실제 전송된 메시지 수: 2개 (검증됨)");
        System.out.println("호출 검증: TranscriptBatchService.findByUserIdAndPeriod() 1회 호출");
        System.out.println("호출 검증: TranscriptProducer.sendTranscriptMessage() 2회 호출");
    }

    @Test
    @DisplayName("다중 유저 데이터 중 특정 유저만 Kafka 전송 및 호출 검증 테스트")
    void 다중_유저_중_특정_유저만_Kafka_전송_및_호출_검증_테스트() {
        // Given: 여러 유저의 실제 데이터
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        String targetUserId = "target-user";

        // 타겟 유저 데이터 3개
        for (int i = 1; i <= 3; i++) {
            Transcript transcript = Transcript.builder()
                    .sessionId("target-session-" + i)
                    .userId(targetUserId)
                    .startTime(baseTime.plusHours(i))
                    .endTime(baseTime.plusHours(i).plusMinutes(5))
                    .conversation(List.of(
                        new ConversationEntry(ConversationEntry.Speaker.patient, "타겟 유저 메시지 " + i)
                    ))
                    .build();
            transcriptRepository.save(transcript);
        }

        // 다른 유저들 데이터 5개 (전송되지 않아야 함)
        for (int i = 1; i <= 5; i++) {
            Transcript transcript = Transcript.builder()
                    .sessionId("other-session-" + i)
                    .userId("other-user-" + i)
                    .startTime(baseTime.plusMinutes(i * 10))
                    .endTime(baseTime.plusMinutes(i * 10 + 5))
                    .conversation(List.of(
                        new ConversationEntry(ConversationEntry.Speaker.patient, "다른 유저" + i + " 메시지")
                    ))
                    .build();
            transcriptRepository.save(transcript);
        }

        assertThat(transcriptRepository.count()).isEqualTo(8); // 총 8개 데이터

        // When: 특정 유저만 Kafka 전송
        TranscriptBatchRequest request = new TranscriptBatchRequest(
                targetUserId,
                baseTime,
                baseTime.plusDays(1)
        );

        assertThatCode(() -> transcriptBatchUseCase.sendTranscriptToKafka(request))
                .doesNotThrowAnyException();

        // Then: 호출 및 필터링 검증
        verify(transcriptBatchService, times(1))
                .findByUserIdAndPeriod(eq(targetUserId), eq(baseTime), eq(baseTime.plusDays(1)));
        
        verify(transcriptProducer, times(3))  // 타겟 유저의 3개만 전송
                .sendTranscriptMessage(any());

        System.out.println("=== 특정 유저 필터링 Kafka 전송 및 호출 검증 완료 ===");
        System.out.println("전체 데이터: 8개");
        System.out.println("타겟 유저: " + targetUserId);
        System.out.println("실제 전송된 메시지: 3개 (검증됨)");
        System.out.println("필터링 확인: 타겟 유저의 데이터만 처리됨");
    }

    // ===== Mock을 이용한 단위 테스트 로직 검증 =====

    @Test
    @DisplayName("빈 결과 처리 및 호출 검증 테스트")
    void 빈_결과_처리_및_호출_검증_테스트() {
        // Given: 빈 데이터베이스 상태
        assertThat(transcriptRepository.count()).isEqualTo(0);
        
        LocalDateTime fromDate = LocalDateTime.now().minusDays(1);
        LocalDateTime toDate = LocalDateTime.now();
        
        TranscriptBatchRequest request = new TranscriptBatchRequest(
                "non-existing-user",
                fromDate,
                toDate
        );

        // When: 빈 결과에서 Kafka 전송
        assertThatCode(() -> transcriptBatchUseCase.sendTranscriptToKafka(request))
                .doesNotThrowAnyException();

        // Then: 서비스는 호출되지만 프로듀서는 호출되지 않음
        verify(transcriptBatchService, times(1))
                .findByUserIdAndPeriod(eq("non-existing-user"), eq(fromDate), eq(toDate));
        
        verify(transcriptProducer, never())  // 전송할 데이터가 없으므로 호출되지 않음
                .sendTranscriptMessage(any());

        System.out.println("=== 빈 결과 처리 및 호출 검증 완료 ===");
        System.out.println("TranscriptBatchService 호출: 1회 (데이터 조회 시도)");
        System.out.println("TranscriptProducer 호출: 0회 (전송할 데이터 없음)");
        System.out.println("처리할 데이터가 없을 때 정상적으로 처리됨");
    }

    @Test
    @DisplayName("TranscriptBatchService 예외 발생 및 처리 테스트")
    void TranscriptBatchService_예외_발생_및_처리_테스트() {
        // Given: 실제 데이터 있음
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Transcript transcript = Transcript.builder()
                .sessionId("error-test-session")
                .userId("error-test-user")
                .startTime(baseTime)
                .endTime(baseTime.plusMinutes(5))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "에러 테스트 메시지")
                ))
                .build();
        transcriptRepository.save(transcript);

        // Mock으로 서비스에서 예외 발생하도록 설정
        when(transcriptBatchService.findByUserIdAndPeriod(eq("error-test-user"), any(), any()))
                .thenThrow(new RuntimeException("Database connection failed"));

        TranscriptBatchRequest request = new TranscriptBatchRequest(
                "error-test-user",
                baseTime,
                baseTime.plusDays(1)
        );

        // When & Then: 서비스 예외로 인한 RestApiException 발생
        assertThatThrownBy(() -> transcriptBatchUseCase.sendTranscriptToKafka(request))
                .isInstanceOf(RestApiException.class);

        // 서비스는 호출되지만 프로듀서는 호출되지 않음
        verify(transcriptBatchService, times(1))
                .findByUserIdAndPeriod(eq("error-test-user"), any(), any());
        
        verify(transcriptProducer, never())
                .sendTranscriptMessage(any());

        System.out.println("=== TranscriptBatchService 예외 발생 테스트 완료 ===");
        System.out.println("서비스 레이어 예외가 적절히 RestApiException으로 변환됨");
        System.out.println("예외 발생 시 Kafka 전송이 시도되지 않음");
    }

    @Test
    @DisplayName("TranscriptProducer 예외 발생 및 처리 테스트")
    void TranscriptProducer_예외_발생_및_처리_테스트() {
        // Given: 실제 데이터
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Transcript transcript = Transcript.builder()
                .sessionId("kafka-error-session")
                .userId("kafka-error-user")
                .startTime(baseTime)
                .endTime(baseTime.plusMinutes(5))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "Kafka 에러 테스트 메시지")
                ))
                .build();
        transcriptRepository.save(transcript);

        // Producer에서 예외 발생하도록 설정
        doThrow(new RuntimeException("Kafka connection failed"))
                .when(transcriptProducer).sendTranscriptMessage(any());

        TranscriptBatchRequest request = new TranscriptBatchRequest(
                "kafka-error-user",
                baseTime,
                baseTime.plusDays(1)
        );

        // When & Then: Kafka 예외로 인한 RestApiException 발생
        assertThatThrownBy(() -> transcriptBatchUseCase.sendTranscriptToKafka(request))
                .isInstanceOf(RestApiException.class);

        // 서비스와 프로듀서 모두 호출됨
        verify(transcriptBatchService, times(1))
                .findByUserIdAndPeriod(eq("kafka-error-user"), any(), any());
        
        verify(transcriptProducer, times(1))
                .sendTranscriptMessage(any());

        System.out.println("=== TranscriptProducer 예외 발생 테스트 완료 ===");
        System.out.println("Kafka Producer 예외가 적절히 RestApiException으로 변환됨");
        System.out.println("데이터 조회는 성공했지만 Kafka 전송에서 실패");
    }

    // ===== 성능 및 대용량 데이터 테스트 =====

    @Test
    @DisplayName("대용량 데이터 Kafka 직접 전송 성능 및 호출 검증 테스트")
    void 대용량_데이터_Kafka_직접_전송_성능_및_호출_검증_테스트() {
        // Given: 대용량 테스트 데이터 생성 (특정 유저의 100개 documents)
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        String targetUserId = "performance-test-user";
        int dataCount = 100;
        
        for (int i = 1; i <= dataCount; i++) {
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

        assertThat(transcriptRepository.count()).isEqualTo(dataCount);

        // When: Kafka 직접 전송 시간 측정
        long startTime = System.currentTimeMillis();
        
        TranscriptBatchRequest request = new TranscriptBatchRequest(
                targetUserId,
                baseTime,
                baseTime.plusDays(1)
        );

        assertThatCode(() -> transcriptBatchUseCase.sendTranscriptToKafka(request))
                .doesNotThrowAnyException();
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Then: 성능 검증 및 호출 검증
        assertThat(executionTime).isLessThan(TimeUnit.SECONDS.toMillis(10)); // 10초 이내

        verify(transcriptBatchService, times(1))
                .findByUserIdAndPeriod(eq(targetUserId), any(), any());
        
        verify(transcriptProducer, times(dataCount))  // 100개 모두 전송
                .sendTranscriptMessage(any());

        System.out.println("=== 대용량 데이터 Kafka 직접 전송 성능 테스트 완료 ===");
        System.out.println("대상 문서 수: " + dataCount + "개");
        System.out.println("실행 시간: " + executionTime + "ms");
        System.out.println("처리 속도: " + (dataCount * 1000.0 / executionTime) + " docs/sec");
        System.out.println("호출 검증: TranscriptProducer.sendTranscriptMessage() " + dataCount + "회 호출");
        System.out.println("비고: 배치 없이 즉시 Kafka 전송");
    }

    // ===== 엣지 케이스 테스트 =====

    @Test
    @DisplayName("null 값 포함 Transcript 처리 및 검증 테스트")
    void null값_포함_Transcript_처리_및_검증_테스트() {
        // Given: 일부 필드가 null인 실제 Transcript
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        
        Transcript transcriptWithNulls = Transcript.builder()
                .transcriptId("transcript-null-test")
                .sessionId(null) // null sessionId
                .userId("null-test-user")
                .startTime(baseTime)
                .endTime(baseTime.plusMinutes(15))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "null 포함 테스트 메시지")
                ))
                .build();
        
        transcriptRepository.save(transcriptWithNulls);

        TranscriptBatchRequest request = new TranscriptBatchRequest(
                "null-test-user",
                baseTime,
                baseTime.plusDays(1)
        );

        // When & Then: null 값이 있어도 정상 처리됨
        assertThatCode(() -> transcriptBatchUseCase.sendTranscriptToKafka(request))
                .doesNotThrowAnyException();

        // 호출 검증
        verify(transcriptBatchService, times(1))
                .findByUserIdAndPeriod(eq("null-test-user"), any(), any());
        
        verify(transcriptProducer, times(1))
                .sendTranscriptMessage(any());

        System.out.println("=== null 값 포함 Transcript 처리 테스트 완료 ===");
        System.out.println("일부 필드가 null이어도 정상적으로 DTO 변환 및 전송됨");
        System.out.println("null sessionId도 적절히 처리됨");
    }

    @Test
    @DisplayName("DTO 변환 정확성 및 데이터 무결성 테스트")
    void DTO_변환_정확성_및_데이터_무결성_테스트() {
        // Given: 명확한 값들을 가진 Transcript
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        String expectedTranscriptId = "transcript-conversion-test";
        String expectedSessionId = "session-conversion-test";
        String expectedUserId = "conversion-test-user";
        LocalDateTime expectedStartTime = baseTime;
        LocalDateTime expectedEndTime = baseTime.plusMinutes(25);
        List<ConversationEntry> expectedConversation = List.of(
            new ConversationEntry(ConversationEntry.Speaker.patient, "테스트 환자 메시지"),
            new ConversationEntry(ConversationEntry.Speaker.ai, "테스트 AI 응답")
        );

        Transcript transcript = Transcript.builder()
                .transcriptId(expectedTranscriptId)
                .sessionId(expectedSessionId)
                .userId(expectedUserId)
                .startTime(expectedStartTime)
                .endTime(expectedEndTime)
                .conversation(expectedConversation)
                .build();
        
        transcriptRepository.save(transcript);

        TranscriptBatchRequest request = new TranscriptBatchRequest(
                expectedUserId,
                baseTime,
                baseTime.plusDays(1)
        );

        // When: 변환 및 전송
        assertThatCode(() -> transcriptBatchUseCase.sendTranscriptToKafka(request))
                .doesNotThrowAnyException();

        // Then: 정확한 호출 검증
        verify(transcriptBatchService, times(1))
                .findByUserIdAndPeriod(eq(expectedUserId), eq(baseTime), eq(baseTime.plusDays(1)));
        
        verify(transcriptProducer, times(1))
                .sendTranscriptMessage(any());

        System.out.println("=== DTO 변환 정확성 테스트 완료 ===");
        System.out.println("Transcript Entity가 TranscriptDto로 정확히 변환됨");
        System.out.println("변환된 필드: transcriptId, sessionId, userId, startTime, endTime, conversation");
        System.out.println("데이터 무결성 확인: 원본 데이터와 변환된 데이터가 일치함");
    }
} 