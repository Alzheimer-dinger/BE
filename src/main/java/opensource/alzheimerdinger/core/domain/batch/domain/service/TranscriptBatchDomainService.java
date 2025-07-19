package opensource.alzheimerdinger.core.domain.batch.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    
    // 날짜 범위 제한 (수정 용이하도록 상수 분리)
    private static final int MAX_DATE_RANGE_DAYS = 31;
    
    // ===== 요청 검증 로직 =====
    
    /**
     * 배치 요청 파라미터 검증 (API용 - userId 필수)
     */
    public void validateBatchRequest(String userId, LocalDateTime fromDate, LocalDateTime toDate) {
        // 필수 파라미터 검증 (fromDate, toDate는 필수)
        if (fromDate == null || toDate == null) {
            throw new RestApiException(BATCH_EMPTY_REQUEST_PARAMS);
        }
        
        // 날짜 범위 검증
        if (fromDate.isAfter(toDate) || fromDate.isEqual(toDate)) {
            throw new RestApiException(BATCH_INVALID_DATE_RANGE);
        }
        
        // 날짜 범위 크기 제한 (수정 용이)
        long daysBetween = ChronoUnit.DAYS.between(fromDate, toDate);
        if (daysBetween > MAX_DATE_RANGE_DAYS) {
            throw new RestApiException(BATCH_DATE_RANGE_TOO_LARGE);
        }
        
        // userId 검증 (API에서는 항상 값이 있어야 함)
        if (userId == null || userId.trim().isEmpty()) {
            throw new RestApiException(BATCH_INVALID_USER_ID);
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
    
    /**
     * 특정 유저 데이터 처리 여부 확인
     */
    public boolean hasSpecificUser(String userId) {
        return userId != null && !userId.trim().isEmpty();
    }
    
    // ===== 내부 헬퍼 메서드 =====
    
    private boolean isJobRunning(String jobName) {
        List<JobInstance> instances = jobExplorer.getJobInstances(jobName, 0, 1);
        if (instances.isEmpty()) return false;
        
        return jobExplorer.getJobExecutions(instances.get(0)).stream()
                .anyMatch(execution -> execution.getStatus().isRunning());
    }
} 