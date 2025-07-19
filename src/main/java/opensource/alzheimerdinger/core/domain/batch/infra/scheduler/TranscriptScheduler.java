package opensource.alzheimerdinger.core.domain.batch.infra.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.application.dto.request.TranscriptBatchRequest;
import opensource.alzheimerdinger.core.domain.batch.application.usecase.TranscriptBatchUseCase;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//Transcript 배치 스케줄러 - 일정 주기로 배치 실행
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class TranscriptScheduler {

    private final TranscriptBatchUseCase transcriptBatchUseCase;

    //매시간 정각에 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 * * * *")
    @Async
    public void executeHourlyTranscriptBatch() {
        String schedulerType = "HOURLY";
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime oneHourAgo = now.minusHours(1);
            String date = oneHourAgo.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            log.info("Starting {} transcript batch for date: {}", schedulerType, date);
            
            TranscriptBatchRequest request = new TranscriptBatchRequest(date, null);
            var response = transcriptBatchUseCase.executeBatch(request);
            
            log.info("Successfully completed {} transcript batch with jobId: {}", schedulerType, response.jobId());
            
        } catch (Exception e) {
            log.error("{} transcript batch failed", schedulerType, e);
        }
    }

    //매일 자정에 실행 - 전날 데이터 전체 처리
    @Scheduled(cron = "0 0 0 * * *")
    @Async
    public void executeDailyTranscriptBatch() {
        String schedulerType = "DAILY";
        
        try {
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0);
            String date = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            log.info("Starting {} transcript batch for date: {}", schedulerType, date);
            
            TranscriptBatchRequest request = new TranscriptBatchRequest(date, null);
            var response = transcriptBatchUseCase.executeBatch(request);
            
            log.info("Successfully completed {} transcript batch with jobId: {}", schedulerType, response.jobId());
            
        } catch (Exception e) {
            log.error("{} transcript batch failed", schedulerType, e);
        }
    }
} 