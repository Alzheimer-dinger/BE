package opensource.alzheimerdinger.core.domain.batch.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.application.dto.request.XXXBatchRequest;
import opensource.alzheimerdinger.core.domain.batch.application.dto.response.XXXBatchResponse;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class XXXUseCase {

    private final JobLauncher jobLauncher;
    private final Job xxxJob;

    public void executeXXXBatch(String date) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("date", date)
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(xxxJob, jobParameters);
            
        } catch (Exception e) {
            log.error("Error executing XXX batch job", e);
            throw new RuntimeException("Batch job execution failed", e);
        }
    }

    public void executeXXXBatchByIds(List<String> ids) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("ids", String.join(",", ids))
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(xxxJob, jobParameters);
            
        } catch (Exception e) {
            log.error("Error executing XXX batch job for ids: {}", ids, e);
            throw new RuntimeException("Batch job execution failed", e);
        }
    }

    public boolean hasTargetIds(XXXBatchRequest request) {
        return request.targetIds() != null && !request.targetIds().isEmpty();
    }

    public XXXBatchResponse createSuccessResponse(String jobId, Integer processedCount, List<String> processedIds) {
        return new XXXBatchResponse(
                jobId,
                "SUCCESS",
                null,
                null,
                processedCount,
                processedIds,
                "배치 처리가 성공적으로 완료되었습니다."
        );
    }

    public XXXBatchResponse createFailureResponse(String jobId, String message) {
        return new XXXBatchResponse(
                jobId,
                "FAILED",
                null,
                null,
                null,
                null,
                message
        );
    }
} 