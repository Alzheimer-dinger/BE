package opensource.alzheimerdinger.core.domain.batch.infra.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.domain.entity.XXXEntity;
import opensource.alzheimerdinger.core.domain.batch.domain.repository.XXXRepository;
import opensource.alzheimerdinger.core.domain.batch.domain.service.XXXService;
import opensource.alzheimerdinger.core.domain.batch.application.dto.XXXDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * XXX 배치 작업 설정
 * 
 * 사용법:
 * 1. XXX를 실제 도메인명으로 변경
 * 2. 필요에 따라 Step 추가/제거
 * 3. Reader, Processor, Writer 구성
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class XXXJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TaskExecutor batchTaskExecutor;
    private final EntityManagerFactory entityManagerFactory;
    private final XXXService xxxService;

    /**
     * XXX 배치 작업 정의
     */
    @Bean
    public Job xxxJob() {
        return new JobBuilder("xxxJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(xxxStep())
                .build();
    }

    /**
     * XXX 배치 Step 정의
     */
    @Bean
    public Step xxxStep() {
        return new StepBuilder("xxxStep", jobRepository)
                .<XXXEntity, XXXDto>chunk(100, transactionManager)
                .reader(xxxReader(null))
                .processor(xxxProcessor())
                .writer(xxxWriter())
                .taskExecutor(batchTaskExecutor)
                .build();
    }

    /**
     * XXX Reader 정의
     */
    @Bean
    @StepScope
    public ItemReader<XXXEntity> xxxReader(@Value("#{jobParameters['date']}") String date) {
        LocalDateTime targetDate = date != null ? 
                LocalDateTime.parse(date + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                LocalDateTime.now().minusDays(1);

        return new JpaPagingItemReaderBuilder<XXXEntity>()
                .name("xxxReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString("SELECT x FROM XXXEntity x WHERE x.createdDate > :date ORDER BY x.id")
                .parameterValues(java.util.Map.of("date", targetDate))
                .build();
    }

    /**
     * XXX Processor 정의
     */
    @Bean
    @StepScope
    public ItemProcessor<XXXEntity, XXXDto> xxxProcessor() {
        return item -> {
            try {
                XXXDto result = xxxService.processXXX(item);
                
                if (result == null || !xxxService.isValidDto(result)) {
                    log.warn("Invalid data filtered out: {}", item.getId());
                    return null;
                }
                
                return result;
                
            } catch (Exception e) {
                log.error("Error processing item: {}", item.getId(), e);
                throw e;
            }
        };
    }

    /**
     * XXX Writer 정의
     */
    @Bean
    @StepScope
    public ItemWriter<XXXDto> xxxWriter() {
        return items -> {
            try {
                log.info("Writing {} items", items.size());
                xxxService.batchSave(items.getItems());
            } catch (Exception e) {
                log.error("Error writing items", e);
                throw e;
            }
        };
    }
} 