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

    // 배치 실행 메인 메서드
    public TranscriptBatchResponse executeBatch(TranscriptBatchRequest request) {
        // 도메인 서비스에서 요청 검증
        transcriptBatchDomainService.validateBatchRequest(request.date(), request.targetIds());
        transcriptBatchDomainService.validateCanExecute("transcriptJob");
        
        String jobId = UUID.randomUUID().toString();
        
        if (transcriptBatchDomainService.hasTargetIds(request.targetIds())) {
            // 특정 ID들만 처리
            return executeTranscriptBatchByIds(request.targetIds(), jobId);
        } else {
            // 날짜 기반 처리
            return executeTranscriptBatch(request.date(), jobId);
        }
    }

    //날짜 기반 Transcript 배치 실행
    private TranscriptBatchResponse executeTranscriptBatch(String date, String jobId) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("date", date)
                    .addString("jobId", jobId)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(transcriptJob, jobParameters);
            
            // JobExecution에서 직접 응답 생성
            return createResponseFromJobExecution(jobExecution, jobId);
            
        } catch (Exception e) {
            log.error("Error executing Transcript batch job for date: {}", date, e);
            throw new RestApiException(BATCH_JOB_EXECUTION_FAILED);
        }
    }

    //ID 기반 Transcript 배치 실행
    private TranscriptBatchResponse executeTranscriptBatchByIds(List<String> ids, String jobId) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("ids", String.join(",", ids))
                    .addString("jobId", jobId)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(transcriptJob, jobParameters);
            
            // JobExecution에서 직접 응답 생성
            return createResponseFromJobExecution(jobExecution, jobId, ids);
            
        } catch (Exception e) {
            log.error("Error executing Transcript batch job for ids: {}", ids, e);
            throw new RestApiException(BATCH_JOB_EXECUTION_FAILED);
        }
    }

    // JobExecution에서 응답 생성
    private TranscriptBatchResponse createResponseFromJobExecution(JobExecution jobExecution, String jobId) {
        return createResponseFromJobExecution(jobExecution, jobId, null);
    }
    
    private TranscriptBatchResponse createResponseFromJobExecution(JobExecution jobExecution, String jobId, List<String> targetIds) {
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
        
        return new TranscriptBatchResponse(
                jobId,
                statusMessage,
                "Transcript batch processing completed",
                processedCount,
                targetIds,
                System.currentTimeMillis()
        );
    }
} 