package opensource.alzheimerdinger.core.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final DataSource dataSource;

    /**
     * Task Executor 설정
     * 배치 작업 실행을 위한 스레드 풀 설정
     * Spring Boot 3.x에서는 JobRepository, JobLauncher 등은 자동 설정됨
     */
    @Bean
    public TaskExecutor batchTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setConcurrencyLimit(10);
        executor.setThreadNamePrefix("batch-executor-");
        return executor;
    }
} 