package opensource.alzheimerdinger.core.domain.batch.infra.job;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.batch.infra.step.TranscriptProcessor;
import opensource.alzheimerdinger.core.domain.batch.infra.step.TranscriptReader;
import opensource.alzheimerdinger.core.domain.batch.infra.step.TranscriptWriter;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Transcript 배치 작업 설정
 * MongoDB에서 Transcript 데이터를 읽어 Kafka로 메시지 전송
 */
@Configuration
@RequiredArgsConstructor
public class TranscriptJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TranscriptReader transcriptReader;
    private final TranscriptProcessor transcriptProcessor;
    private final TranscriptWriter transcriptWriter;

    //Transcript 배치 작업 정의
    @Bean
    public Job transcriptProcessingJob() {
        return new JobBuilder("transcriptJob", jobRepository)
                .preventRestart()
                .start(transcriptStep())
                .build();
    }

    //Transcript 배치 Step 정의
    @Bean
    public Step transcriptStep() {
        return new StepBuilder("transcriptStep", jobRepository)
                .<Transcript, opensource.alzheimerdinger.core.domain.batch.application.dto.TranscriptDto>chunk(100, transactionManager)
                .reader(transcriptItemReader(null))
                .processor(transcriptItemProcessor())
                .writer(transcriptItemWriter())
                .build();
    }

    //Transcript Reader 정의 - MongoDB에서 데이터 읽기
    @Bean
    @StepScope
    public ItemReader<Transcript> transcriptItemReader(@Value("#{jobParameters['date'] ?: null}") String date) {
        return transcriptReader.createDateBasedReader(date);
    }

    //Transcript Processor 정의 - 데이터 변환
    @Bean
    public ItemProcessor<Transcript, opensource.alzheimerdinger.core.domain.batch.application.dto.TranscriptDto> transcriptItemProcessor() {
        return transcriptProcessor.createDefaultProcessor();
    }

    //Transcript Writer 정의 - Kafka로 메시지 전송
    @Bean
    public ItemWriter<opensource.alzheimerdinger.core.domain.batch.application.dto.TranscriptDto> transcriptItemWriter() {
        return transcriptWriter.createKafkaWriter();
    }
} 