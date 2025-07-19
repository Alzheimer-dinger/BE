package opensource.alzheimerdinger.core.domain.batch.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.application.dto.request.TranscriptBatchRequest;
import opensource.alzheimerdinger.core.domain.batch.application.dto.response.TranscriptBatchResponse;
import opensource.alzheimerdinger.core.domain.batch.domain.service.TranscriptBatchDomainService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static opensource.alzheimerdinger.core.global.exception.code.status.BatchErrorStatus.*;

//Transcript 배치 실행을 위한 UseCase
@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptBatchUseCase {

    private final JobLauncher jobLauncher;
    private final Job transcriptJob;
    private final TranscriptBatchDomainService transcriptBatchDomainService;

    // API용 - 특정 유저 배치 실행
    public TranscriptBatchResponse executeBatch(TranscriptBatchRequest request) {
        // 도메인 서비스에서 요청 검증
        transcriptBatchDomainService.validateBatchRequest(
            request.userId(), request.fromDate(), request.toDate());
        transcriptBatchDomainService.validateCanExecute("transcriptJob");
        
        String jobId = UUID.randomUUID().toString();
        
        // 특정 유저 처리
        return executeTranscriptBatch(request.userId(), request.fromDate(), request.toDate(), jobId);
    }

    // 스케줄러용 - 모든 유저 배치 실행
    public TranscriptBatchResponse executeScheduledBatch(LocalDateTime fromDate, LocalDateTime toDate) {
        // 기본 날짜 검증만 수행 (userId 검증 제외)
        if (fromDate == null || toDate == null) {
            throw new RestApiException(BATCH_EMPTY_REQUEST_PARAMS);
        }
        if (fromDate.isAfter(toDate) || fromDate.isEqual(toDate)) {
            throw new RestApiException(BATCH_INVALID_DATE_RANGE);
        }
        
        transcriptBatchDomainService.validateCanExecute("transcriptJob");
        
        String jobId = UUID.randomUUID().toString();
        
        // 모든 유저 처리 (userId = null)
        return executeTranscriptBatch(null, fromDate, toDate, jobId);
    }

    //유저별 기간 Transcript 배치 실행 (내부 공통 메서드)
    private TranscriptBatchResponse executeTranscriptBatch(String userId, LocalDateTime fromDate, LocalDateTime toDate, String jobId) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("userId", userId != null ? userId : "")
                    .addString("fromDate", fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .addString("toDate", toDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .addString("jobId", jobId)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(transcriptJob, jobParameters);
            
            // JobExecution에서 직접 응답 생성
            return createResponseFromJobExecution(jobExecution, jobId, userId, fromDate, toDate);
            
        } catch (Exception e) {
            log.error("Error executing Transcript batch job for user: {}, period: {} ~ {}", 
                    userId, fromDate, toDate, e);
            throw new RestApiException(BATCH_JOB_EXECUTION_FAILED);
        }
    }

    // JobExecution에서 응답 생성
    private TranscriptBatchResponse createResponseFromJobExecution(
            JobExecution jobExecution, String jobId, String userId, LocalDateTime fromDate, LocalDateTime toDate) {
        BatchStatus status = jobExecution.getStatus();
        String statusMessage;
        
        // Spring Batch의 BatchStatus 직접 활용
        switch (status) {
            case COMPLETED -> statusMessage = "SUCCESS";
            case FAILED -> statusMessage = "FAILURE";
            case STARTED, STARTING -> statusMessage = "RUNNING";
            default -> statusMessage = "UNKNOWN";
        }
        
        // 처리 건수 계산
        int processedCount = jobExecution.getStepExecutions().stream()
                .mapToInt(step -> (int) step.getWriteCount())
                .sum();
        
        // 처리된 ID 목록 (실제로는 처리된 transcript ID들을 수집해야 하지만 일단 null로)
        List<String> processedIds = null;
        
        return new TranscriptBatchResponse(
                jobId,
                statusMessage,
                String.format("Transcript batch processing completed for user: %s, period: %s ~ %s", 
                        userId != null ? userId : "ALL", fromDate, toDate),
                processedCount,
                processedIds,
                System.currentTimeMillis()
        );
    }
} 