package opensource.alzheimerdinger.core.domain.reminder.infra.batch;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.reminder.domain.entity.Reminder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ReminderBatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;
    private final ReminderItemWriter itemWriter;

    @Bean
    @StepScope
    public ReminderKeysetItemReader reminderReader() {
        return new ReminderKeysetItemReader(jdbcTemplate, entityManager, 500);
    }

    @Bean
    public Step reminderStep() {
        return new StepBuilder("reminderStep", jobRepository)
                .<Reminder, Reminder>chunk(500, transactionManager)
                .reader(reminderReader())
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Job reminderJob() {
        return new JobBuilder("reminderJob", jobRepository)
                .start(reminderStep())
                .build();
    }
}
