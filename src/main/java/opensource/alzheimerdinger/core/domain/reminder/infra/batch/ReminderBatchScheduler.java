package opensource.alzheimerdinger.core.domain.reminder.infra.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ReminderBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job reminderJob;

    @Scheduled(cron = "0 */1 * * * *", zone = "Asia/Seoul")
    public void run() throws Exception {
        log.info("Starting Reminder Batch Job");

        jobLauncher.run(
                reminderJob,
                new JobParametersBuilder()
                        .addLong("ts", System.currentTimeMillis())
                        .toJobParameters());
    }
}