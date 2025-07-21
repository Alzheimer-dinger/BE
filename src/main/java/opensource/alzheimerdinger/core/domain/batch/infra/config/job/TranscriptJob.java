package opensource.alzheimerdinger.core.domain.batch.infra.config.job;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.batch.application.dto.TranscriptDto;
import opensource.alzheimerdinger.core.domain.batch.infra.config.step.TranscriptProcessor;
import opensource.alzheimerdinger.core.domain.batch.infra.config.step.TranscriptReader;
import opensource.alzheimerdinger.core.domain.batch.infra.config.step.TranscriptWriter;
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

 //Transcript 배치 작업 설정 및 MongoDB에서 특정 유저의 특정 기간 Transcript 데이터를 읽어 Kafka로 메시지 전송
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
                .<Transcript, TranscriptDto>chunk(100, transactionManager)
                .reader(transcriptItemReader(null, null))
                .processor(transcriptItemProcessor())
                .writer(transcriptItemWriter())
                .build();
    }

    //Transcript Reader 정의 - MongoDB에서 모든 유저 기간 데이터 읽기
    @Bean
    @StepScope
    public ItemReader<Transcript> transcriptItemReader(
            @Value("#{jobParameters['fromDate'] ?: null}") String fromDate,
            @Value("#{jobParameters['toDate'] ?: null}") String toDate) {
        return transcriptReader.createAllUsersReader(fromDate, toDate);
    }

    //Transcript Processor 정의 - 데이터 변환
    @Bean
    public ItemProcessor<Transcript, TranscriptDto> transcriptItemProcessor() {
        return transcriptProcessor.createDefaultProcessor();
    }

    //Transcript Writer 정의 - Kafka로 메시지 전송
    @Bean
    public ItemWriter<TranscriptDto> transcriptItemWriter() {
        return transcriptWriter.createKafkaWriter();
    }
} 