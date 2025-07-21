package opensource.alzheimerdinger.core.domain.batch.infra.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.domain.service.TranscriptBatchService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static opensource.alzheimerdinger.core.global.exception.code.status.BatchErrorStatus.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptBatchConfig {
    
    private final JobLauncher jobLauncher;
    private final Job transcriptJob;
    private final JobExplorer jobExplorer;
    private final TranscriptBatchService transcriptBatchService;
    

    // 배치 실행 가능 여부 확인
    public void validateCanExecute(String jobName) {
        List<JobInstance> instances = jobExplorer.getJobInstances(jobName, 0, 1);
        if (!instances.isEmpty()) {
            boolean isRunning = jobExplorer.getJobExecutions(instances.get(0)).stream()
                    .anyMatch(execution -> execution.getStatus().isRunning());
            
            if (isRunning) {
                throw new RestApiException(BATCH_JOB_ALREADY_RUNNING);
            }
        }
    }
    

    // 모든 유저 배치 실행
    public void executeScheduledBatch(LocalDateTime fromDate, LocalDateTime toDate) {
        // 기본 날짜 검증
        if (fromDate == null || toDate == null) {
            throw new RestApiException(BATCH_EMPTY_REQUEST_PARAMS);
        }
        if (fromDate.isAfter(toDate) || fromDate.isEqual(toDate)) {
            throw new RestApiException(BATCH_INVALID_DATE_RANGE);
        }
        
        validateCanExecute("transcriptJob");
        
        try {
            String jobId = UUID.randomUUID().toString();
            
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fromDate", fromDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .addString("toDate", toDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .addString("jobId", jobId)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(transcriptJob, jobParameters);
            
            // 배치 실행 결과 로깅
            logBatchResult(jobExecution, jobId, fromDate, toDate);
            
        } catch (Exception e) {
            log.error("Error executing all users batch job for period: {} ~ {}", 
                    fromDate, toDate, e);
            throw new RestApiException(BATCH_JOB_EXECUTION_FAILED);
        }
    }
    

    //배치 실행 결과 로깅

    private void logBatchResult(JobExecution jobExecution, String jobId, 
                               LocalDateTime fromDate, LocalDateTime toDate) {
        BatchStatus status = jobExecution.getStatus();
        String statusMessage;
        
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
        
        log.info("Transcript batch execution completed - JobId: {}, Status: {}, User: ALL, Period: {} ~ {}, ProcessedCount: {}", 
                jobId, statusMessage, fromDate, toDate, processedCount);
    }
} 