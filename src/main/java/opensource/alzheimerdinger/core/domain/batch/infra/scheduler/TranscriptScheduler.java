package opensource.alzheimerdinger.core.domain.batch.infra.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.application.usecase.TranscriptBatchUseCase;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//Transcript 배치 스케줄러 - 매일 새벽 2시에 전날 데이터 처리
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class TranscriptScheduler {

    private final TranscriptBatchUseCase transcriptBatchUseCase;

    //매일 새벽 2시에 실행 - 전날 하루 전체 데이터 처리 (모든 유저)
    @Scheduled(cron = "0 0 2 * * *")
    @Async
    public void executeDailyTranscriptBatch() {
        String schedulerType = "DAILY";
        
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterdayStart = now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime todayStart = now.withHour(0).withMinute(0).withSecond(0).withNano(0);

            log.info("Starting {} transcript batch for period: {} ~ {} (ALL USERS)", 
                    schedulerType, yesterdayStart, todayStart);
            
            // 스케줄러 전용 메서드로 모든 유저 데이터 처리
            var response = transcriptBatchUseCase.executeScheduledBatch(yesterdayStart, todayStart);
            
            log.info("Successfully completed {} transcript batch with jobId: {}, processed: {} documents", 
                    schedulerType, response.jobId(), response.processedCount());
            
        } catch (Exception e) {
            log.error("{} transcript batch failed", schedulerType, e);
        }
    }
} 