package opensource.alzheimerdinger.core.domain.batch.infra.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.application.usecase.XXXUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class XXXScheduler {

    private final XXXUseCase xxxUseCase;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 일일 배치 실행 (매일 오전 2시)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void runDailyXXXBatch() {
        try {
            String yesterday = LocalDateTime.now().minusDays(1).format(formatter);
            xxxUseCase.executeXXXBatch(yesterday);
        } catch (Exception e) {
            log.error("Error in daily XXX batch execution", e);
        }
    }
} 