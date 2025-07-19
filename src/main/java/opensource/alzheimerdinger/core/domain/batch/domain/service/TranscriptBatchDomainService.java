package opensource.alzheimerdinger.core.domain.batch.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static opensource.alzheimerdinger.core.global.exception.code.status.BatchErrorStatus.*;

/**
 * 배치 관련 비즈니스 로직을 집중시키는 도메인 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TranscriptBatchDomainService {
    
    private final JobExplorer jobExplorer;
    
    // ===== 요청 검증 로직 =====
    
    /**
     * 배치 요청 파라미터 검증
     */
    public void validateBatchRequest(String date, List<String> targetIds) {
        // 필수 파라미터 검증
        if ((date == null || date.trim().isEmpty()) && 
            (targetIds == null || targetIds.isEmpty())) {
            throw new RestApiException(BATCH_EMPTY_REQUEST_PARAMS);
        }
        
        // 대상 ID 검증
        if (targetIds != null && 
            targetIds.stream().anyMatch(id -> id == null || id.trim().isEmpty())) {
            throw new RestApiException(BATCH_INVALID_TARGET_IDS);
        }
        
        // 날짜 형식 검증
        if (date != null) {
            try {
                LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (DateTimeParseException e) {
                throw new RestApiException(BATCH_INVALID_DATE_FORMAT);
            }
        }
    }
    
    // ===== 실행 상태 확인 로직 =====
    
    /**
     * 배치 실행 가능 여부 확인
     */
    public void validateCanExecute(String jobName) {
        if (isJobRunning(jobName)) {
            throw new RestApiException(BATCH_JOB_ALREADY_RUNNING);
        }
    }
    
    // ===== 공통 유틸리티 =====
    
    public boolean hasTargetIds(List<String> targetIds) {
        return targetIds != null && !targetIds.isEmpty();
    }
    
    // ===== 내부 헬퍼 메서드 =====
    
    private boolean isJobRunning(String jobName) {
        List<JobInstance> instances = jobExplorer.getJobInstances(jobName, 0, 1);
        if (instances.isEmpty()) return false;
        
        return jobExplorer.getJobExecutions(instances.get(0)).stream()
                .anyMatch(execution -> execution.getStatus().isRunning());
    }
} 