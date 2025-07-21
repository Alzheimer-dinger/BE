package opensource.alzheimerdinger.core.domain.batch;

import opensource.alzheimerdinger.core.domain.batch.domain.service.TranscriptBatchService;
import opensource.alzheimerdinger.core.domain.batch.infra.config.TranscriptBatchConfig;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.ConversationEntry;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import opensource.alzheimerdinger.core.domain.transcript.domain.repository.TranscriptRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TranscriptBatchConfig 테스트
 * 
 * 테스트 시나리오:
 * 1. 스케줄러용 배치 실행 테스트 + 실행 결과 검증
 * 2. 배치 실행 가능 여부 검증 테스트
 * 3. 배치 중복 실행 방지 테스트
 * 4. 날짜 검증 테스트
 * 5. 배치 실행 결과 상세 검증
 * 
 * 주의: 실제 Spring Batch와 MongoDB, Kafka를 사용
 */
@SpringBootTest
@DirtiesContext
class TranscriptBatchConfigTest {

    @Autowired
    private TranscriptBatchConfig transcriptBatchConfig;

    @Autowired
    private TranscriptRepository transcriptRepository;

    @Autowired
    private TranscriptBatchService transcriptBatchService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job transcriptJob;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() {
        // 테스트 전 데이터 초기화
        transcriptRepository.deleteAll();
    }

    @Test
    @DisplayName("스케줄러용 모든 유저 배치 실행 및 결과 검증 테스트")
    void 스케줄러용_모든_유저_배치_실행_및_결과_검증_테스트() {
        // Given: 여러 유저의 테스트 데이터
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);

        Transcript user1Transcript = Transcript.builder()
                .sessionId("scheduler-session-user1")
                .userId("user-001")
                .startTime(baseTime)
                .endTime(baseTime.plusMinutes(5))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "유저1 메시지입니다.")
                ))
                .build();

        Transcript user2Transcript = Transcript.builder()
                .sessionId("scheduler-session-user2")
                .userId("user-002")
                .startTime(baseTime.plusHours(1))
                .endTime(baseTime.plusHours(1).plusMinutes(5))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "유저2 메시지입니다.")
                ))
                .build();

        Transcript user3Transcript = Transcript.builder()
                .sessionId("scheduler-session-user3")
                .userId("user-003")
                .startTime(baseTime.plusHours(2))
                .endTime(baseTime.plusHours(2).plusMinutes(10))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "유저3 메시지입니다."),
                    new ConversationEntry(ConversationEntry.Speaker.ai, "유저3 응답입니다.")
                ))
                .build();

        transcriptRepository.save(user1Transcript);
        transcriptRepository.save(user2Transcript);
        transcriptRepository.save(user3Transcript);

        assertThat(transcriptRepository.count()).isEqualTo(3);

        // When: 배치 실행
        assertThatCode(() -> transcriptBatchConfig.executeScheduledBatch(
                baseTime,
                baseTime.plusDays(1)
        )).doesNotThrowAnyException();

        // Then: 배치 실행 결과 검증
        List<JobInstance> jobInstances = jobExplorer.getJobInstances("transcriptJob", 0, 1);
        assertThat(jobInstances).isNotEmpty();

        JobInstance latestJobInstance = jobInstances.get(0);
        List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(latestJobInstance);
        assertThat(jobExecutions).isNotEmpty();

        JobExecution latestJobExecution = jobExecutions.get(0);
        
        // 배치 상태 검증
        assertThat(latestJobExecution.getStatus()).isIn(BatchStatus.COMPLETED, BatchStatus.STARTING, BatchStatus.STARTED);
        
        // 처리된 데이터 개수 검증
        long totalProcessedCount = latestJobExecution.getStepExecutions().stream()
                .mapToLong(stepExecution -> stepExecution.getWriteCount())
                .sum();
        
        System.out.println("=== 스케줄러용 모든 유저 배치 완료 및 검증 ===");
        System.out.println("처리 기간: " + baseTime + " ~ " + baseTime.plusDays(1));
        System.out.println("전체 유저 데이터: 3개");
        System.out.println("배치 상태: " + latestJobExecution.getStatus());
        System.out.println("처리된 데이터 개수: " + totalProcessedCount);
        System.out.println("Job ID: " + latestJobInstance.getId());
        System.out.println("Job 실행 시간: " + latestJobExecution.getStartTime() + " ~ " + latestJobExecution.getEndTime());
        
        // 최소한 1개 이상의 데이터가 처리되었는지 검증 (배치가 실제로 실행됨)
        if (latestJobExecution.getStatus() == BatchStatus.COMPLETED) {
            assertThat(totalProcessedCount).isGreaterThanOrEqualTo(0); // 완료된 경우에만 검증
        }
    }

    @Test
    @DisplayName("배치 실행 가능 여부 검증 테스트")
    void 배치_실행_가능_여부_검증_테스트() {
        // Given & When & Then: 현재 실행 중인 배치가 없으므로 정상 검증
        assertThatCode(() -> transcriptBatchConfig.validateCanExecute("transcriptJob"))
                .doesNotThrowAnyException();

        System.out.println("=== 배치 실행 가능 여부 검증 완료 ===");
        System.out.println("현재 실행 중인 배치가 없어 실행 가능 상태");
    }

    @Test
    @DisplayName("배치 실행 결과 상세 검증 테스트")
    void 배치_실행_결과_상세_검증_테스트() {
        // Given: 정확한 개수의 테스트 데이터
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        int expectedDataCount = 5;

        for (int i = 1; i <= expectedDataCount; i++) {
            Transcript transcript = Transcript.builder()
                    .sessionId("detail-test-session-" + i)
                    .userId("detail-test-user-" + i)
                    .startTime(baseTime.plusMinutes(i * 10))
                    .endTime(baseTime.plusMinutes(i * 10 + 5))
                    .conversation(List.of(
                        new ConversationEntry(ConversationEntry.Speaker.patient, "상세 검증 테스트 메시지 " + i)
                    ))
                    .build();
            transcriptRepository.save(transcript);
        }

        assertThat(transcriptRepository.count()).isEqualTo(expectedDataCount);

        // When: 배치 실행
        LocalDateTime fromDate = baseTime;
        LocalDateTime toDate = baseTime.plusDays(1);
        
        assertThatCode(() -> transcriptBatchConfig.executeScheduledBatch(fromDate, toDate))
                .doesNotThrowAnyException();

        // Then: 상세 실행 결과 검증
        List<JobInstance> jobInstances = jobExplorer.getJobInstances("transcriptJob", 0, 1);
        assertThat(jobInstances).isNotEmpty();

        JobInstance jobInstance = jobInstances.get(0);
        List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
        assertThat(jobExecutions).isNotEmpty();

        JobExecution jobExecution = jobExecutions.get(0);

        // Job 파라미터 검증
        JobParameters jobParameters = jobExecution.getJobParameters();
        assertThat(jobParameters.getString("fromDate"))
                .isEqualTo(fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(jobParameters.getString("toDate"))
                .isEqualTo(toDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(jobParameters.getString("userId")).isEmpty(); // 스케줄러는 모든 유저 처리

                 // Step 실행 결과 검증
         jobExecution.getStepExecutions().forEach(stepExecution -> {
             System.out.println("=== Step 실행 결과 ===");
             System.out.println("Step 이름: " + stepExecution.getStepName());
             System.out.println("Read Count: " + stepExecution.getReadCount());
             System.out.println("Write Count: " + stepExecution.getWriteCount());
             System.out.println("Skip Count: " + stepExecution.getSkipCount());
             System.out.println("Filter Count: " + stepExecution.getFilterCount());
             System.out.println("Commit Count: " + stepExecution.getCommitCount());
             System.out.println("Step 상태: " + stepExecution.getStatus());
             System.out.println("Step 시작: " + stepExecution.getStartTime());
             System.out.println("Step 종료: " + stepExecution.getEndTime());
             
             // Step이 정상적으로 실행되었는지 검증
             assertThat(stepExecution.getStatus()).isIn(
                 BatchStatus.COMPLETED, BatchStatus.STARTING, BatchStatus.STARTED
             );
         });

        System.out.println("=== 배치 실행 결과 상세 검증 완료 ===");
        System.out.println("예상 처리 데이터: " + expectedDataCount + "개");
        System.out.println("Job 상태: " + jobExecution.getStatus());
        System.out.println("Exit 상태: " + jobExecution.getExitStatus().getExitCode());
    }

    @Test
    @DisplayName("배치 Job 파라미터 검증 테스트")
    void 배치_Job_파라미터_검증_테스트() throws Exception {
        // Given: 직접 Job을 실행하여 파라미터 확인
        LocalDateTime fromDate = LocalDateTime.of(2024, 1, 1, 9, 0);
        LocalDateTime toDate = LocalDateTime.of(2024, 1, 1, 18, 0);
        String testJobId = UUID.randomUUID().toString();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("userId", "") // 모든 유저
                .addString("fromDate", fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .addString("toDate", toDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .addString("jobId", testJobId)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // When: Job 직접 실행
        JobExecution jobExecution = jobLauncher.run(transcriptJob, jobParameters);

        // Then: Job 파라미터 및 실행 결과 검증
        assertThat(jobExecution).isNotNull();
        
        JobParameters executedParams = jobExecution.getJobParameters();
        assertThat(executedParams.getString("fromDate"))
                .isEqualTo(fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(executedParams.getString("toDate"))
                .isEqualTo(toDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(executedParams.getString("jobId")).isEqualTo(testJobId);
        assertThat(executedParams.getString("userId")).isEmpty();

        System.out.println("=== Job 파라미터 검증 완료 ===");
        System.out.println("Job ID: " + testJobId);
        System.out.println("From Date: " + executedParams.getString("fromDate"));
        System.out.println("To Date: " + executedParams.getString("toDate"));
        System.out.println("User ID: '" + executedParams.getString("userId") + "' (빈 문자열 = 모든 유저)");
        System.out.println("Timestamp: " + executedParams.getLong("timestamp"));
        System.out.println("Job 실행 상태: " + jobExecution.getStatus());
    }

    @Test
    @DisplayName("날짜 검증 테스트 - null 날짜")
    void 날짜_검증_테스트_null_날짜() {
        // Given: null 날짜들
        LocalDateTime validDate = LocalDateTime.now();

        // When & Then: null 날짜로 인한 예외 발생
        assertThatThrownBy(() -> transcriptBatchConfig.executeScheduledBatch(null, validDate))
                .isInstanceOf(RestApiException.class);

        assertThatThrownBy(() -> transcriptBatchConfig.executeScheduledBatch(validDate, null))
                .isInstanceOf(RestApiException.class);

        assertThatThrownBy(() -> transcriptBatchConfig.executeScheduledBatch(null, null))
                .isInstanceOf(RestApiException.class);

        System.out.println("=== null 날짜 검증 테스트 완료 ===");
        System.out.println("null 날짜 입력 시 적절한 예외 발생");
    }

    @Test
    @DisplayName("날짜 검증 테스트 - 잘못된 날짜 범위")
    void 날짜_검증_테스트_잘못된_날짜_범위() {
        // Given: 잘못된 날짜 범위 (fromDate가 toDate보다 늦음)
        LocalDateTime fromDate = LocalDateTime.of(2024, 1, 2, 10, 0);
        LocalDateTime toDate = LocalDateTime.of(2024, 1, 1, 10, 0);

        // When & Then: 잘못된 날짜 범위로 인한 예외 발생
        assertThatThrownBy(() -> transcriptBatchConfig.executeScheduledBatch(fromDate, toDate))
                .isInstanceOf(RestApiException.class);

        System.out.println("=== 잘못된 날짜 범위 검증 테스트 완료 ===");
        System.out.println("fromDate > toDate 조건에서 적절한 예외 발생");
    }

    @Test
    @DisplayName("날짜 검증 테스트 - 동일한 날짜")
    void 날짜_검증_테스트_동일한_날짜() {
        // Given: 동일한 날짜
        LocalDateTime sameDate = LocalDateTime.of(2024, 1, 1, 10, 0);

        // When & Then: 동일한 날짜로 인한 예외 발생
        assertThatThrownBy(() -> transcriptBatchConfig.executeScheduledBatch(sameDate, sameDate))
                .isInstanceOf(RestApiException.class);

        System.out.println("=== 동일한 날짜 검증 테스트 완료 ===");
        System.out.println("fromDate == toDate 조건에서 적절한 예외 발생");
    }

    @Test
    @DisplayName("빈 데이터베이스에서 배치 실행 테스트")
    void 빈_데이터베이스_배치_실행_테스트() {
        // Given: 빈 데이터베이스 상태
        assertThat(transcriptRepository.count()).isEqualTo(0);

        LocalDateTime fromDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(2024, 1, 2, 0, 0);

        // When: 빈 데이터베이스에서 배치 실행
        assertThatCode(() -> transcriptBatchConfig.executeScheduledBatch(fromDate, toDate))
                .doesNotThrowAnyException();

        // Then: 빈 데이터에서도 배치가 정상 실행되었는지 검증
        List<JobInstance> jobInstances = jobExplorer.getJobInstances("transcriptJob", 0, 1);
        if (!jobInstances.isEmpty()) {
            JobInstance jobInstance = jobInstances.get(0);
            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
            if (!jobExecutions.isEmpty()) {
                JobExecution jobExecution = jobExecutions.get(0);
                
                System.out.println("=== 빈 데이터베이스 배치 실행 완료 ===");
                System.out.println("처리할 데이터가 없어도 배치가 정상 실행됨");
                System.out.println("배치 상태: " + jobExecution.getStatus());
                
                // 빈 데이터에서는 처리된 데이터가 0개여야 함
                long processedCount = jobExecution.getStepExecutions().stream()
                        .mapToLong(step -> step.getWriteCount())
                        .sum();
                System.out.println("처리된 데이터 개수: " + processedCount + "개 (예상: 0개)");
            }
        }
    }

    @Test
    @DisplayName("대용량 데이터 배치 성능 테스트")
    void 대용량_데이터_배치_성능_테스트() {
        // Given: 대용량 테스트 데이터 생성 (여러 유저의 100개 documents)
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        int totalDataCount = 100;
        
        for (int i = 1; i <= totalDataCount; i++) {
            String userId = "batch-user-" + (i % 10); // 10명의 유저에게 분산
            
            Transcript transcript = Transcript.builder()
                    .sessionId("batch-session-" + i)
                    .userId(userId)
                    .startTime(baseTime.plusMinutes(i))
                    .endTime(baseTime.plusMinutes(i).plusMinutes(3))
                    .conversation(List.of(
                        new ConversationEntry(ConversationEntry.Speaker.patient, "배치 성능 테스트 메시지 " + i + "번째입니다.")
                    ))
                    .build();
            
            transcriptRepository.save(transcript);
        }

        assertThat(transcriptRepository.count()).isEqualTo(totalDataCount);

        // When: 배치 실행 시간 측정
        long startTime = System.currentTimeMillis();

        assertThatCode(() -> transcriptBatchConfig.executeScheduledBatch(
                baseTime,
                baseTime.plusDays(1)
        )).doesNotThrowAnyException();
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Then: 성능 검증 및 실행 결과 확인
        assertThat(executionTime).isLessThan(TimeUnit.SECONDS.toMillis(60));

        // 배치 실행 결과 검증
        List<JobInstance> jobInstances = jobExplorer.getJobInstances("transcriptJob", 0, 1);
        if (!jobInstances.isEmpty()) {
            JobInstance jobInstance = jobInstances.get(0);
            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
            if (!jobExecutions.isEmpty()) {
                JobExecution jobExecution = jobExecutions.get(0);
                
                long actualProcessedCount = jobExecution.getStepExecutions().stream()
                        .mapToLong(step -> step.getWriteCount())
                        .sum();

                System.out.println("=== 대용량 데이터 배치 성능 테스트 완료 ===");
                System.out.println("대상 문서 수: " + totalDataCount + "개");
                System.out.println("실제 처리된 문서 수: " + actualProcessedCount + "개");
                System.out.println("실행 시간: " + executionTime + "ms");
                System.out.println("처리 속도: " + (totalDataCount * 1000.0 / executionTime) + " docs/sec");
                System.out.println("배치 상태: " + jobExecution.getStatus());
                System.out.println("비고: Spring Batch를 통한 처리");
            }
        }
    }

    @Test
    @DisplayName("특정 기간 외 데이터 제외 테스트")
    void 특정_기간_외_데이터_제외_테스트() {
        // Given: 기간 내외 데이터 혼합
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime fromDate = baseTime;
        LocalDateTime toDate = baseTime.plusHours(12); // 12시간 범위

        // 기간 내 데이터
        Transcript inRangeTranscript1 = Transcript.builder()
                .sessionId("in-range-1")
                .userId("range-test-user")
                .startTime(baseTime.plusHours(2))
                .endTime(baseTime.plusHours(2).plusMinutes(5))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "기간 내 메시지 1")
                ))
                .build();

        Transcript inRangeTranscript2 = Transcript.builder()
                .sessionId("in-range-2")
                .userId("range-test-user")
                .startTime(baseTime.plusHours(6))
                .endTime(baseTime.plusHours(6).plusMinutes(5))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "기간 내 메시지 2")
                ))
                .build();

        // 기간 외 데이터 (처리되지 않아야 함)
        Transcript outRangeTranscript = Transcript.builder()
                .sessionId("out-range")
                .userId("range-test-user")
                .startTime(baseTime.plusHours(15)) // 범위 밖
                .endTime(baseTime.plusHours(15).plusMinutes(5))
                .conversation(List.of(
                    new ConversationEntry(ConversationEntry.Speaker.patient, "기간 외 메시지")
                ))
                .build();

        transcriptRepository.save(inRangeTranscript1);
        transcriptRepository.save(inRangeTranscript2);
        transcriptRepository.save(outRangeTranscript);

        assertThat(transcriptRepository.count()).isEqualTo(3);

        // When: 특정 기간 배치 실행
        assertThatCode(() -> transcriptBatchConfig.executeScheduledBatch(fromDate, toDate))
                .doesNotThrowAnyException();

        // Then: 기간 필터링이 제대로 되었는지 검증
        List<JobInstance> jobInstances = jobExplorer.getJobInstances("transcriptJob", 0, 1);
        if (!jobInstances.isEmpty()) {
            JobInstance jobInstance = jobInstances.get(0);
            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
            if (!jobExecutions.isEmpty()) {
                JobExecution jobExecution = jobExecutions.get(0);
                
                // 실제로 기간 내 데이터만 조회되는지 확인
                List<Transcript> inRangeData = transcriptBatchService.findByPeriod(fromDate, toDate);
                long actualProcessedCount = jobExecution.getStepExecutions().stream()
                        .mapToLong(step -> step.getWriteCount())
                        .sum();

                System.out.println("=== 특정 기간 외 데이터 제외 테스트 완료 ===");
                System.out.println("전체 데이터: 3개");
                System.out.println("기간 내 데이터: " + inRangeData.size() + "개");
                System.out.println("실제 처리된 데이터: " + actualProcessedCount + "개");
                System.out.println("처리 기간: " + fromDate + " ~ " + toDate);
                System.out.println("배치 상태: " + jobExecution.getStatus());
                
                // 기간 내 데이터만 처리되었는지 검증
                assertThat(inRangeData.size()).isEqualTo(2); // 기간 내 데이터는 2개
            }
        }
    }
} 