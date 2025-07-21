package opensource.alzheimerdinger.core.domain.batch.infra.config.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.application.dto.TranscriptDto;
import opensource.alzheimerdinger.core.domain.batch.infra.kafka.TranscriptProducer;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

//Kafka로 Transcript 메시지를 전송하는 Writer
@Slf4j
@Component
@RequiredArgsConstructor
public class TranscriptWriter {

    private final TranscriptProducer transcriptProducer;

    //Kafka로 메시지 전송하는 Writer (TranscriptDto 처리)
    public ItemWriter<TranscriptDto> createKafkaWriter() {
        return new ItemWriter<TranscriptDto>() {
            @Override
            public void write(Chunk<? extends TranscriptDto> chunk) throws Exception {
                for (TranscriptDto item : chunk.getItems()) {
                    try {
                        // 순수하게 Kafka 전송만 담당
                        transcriptProducer.sendTranscriptMessageSync(item);
                        
                        log.debug("Processed transcript message: transcriptId={}, sessionId={}, conversationCount={}", 
                                item.transcriptId(), item.sessionId(), item.conversation().size());
                    } catch (Exception e) {
                        log.error("Failed to process transcript message: transcriptId={}", 
                                item.transcriptId(), e);
                        throw e;
                    }
                }
                
                log.info("Successfully processed {} transcript documents", chunk.size());
            }
        };
    }
} 