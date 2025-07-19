package opensource.alzheimerdinger.core.domain.batch.infra.step;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.batch.domain.service.TranscriptBatchService;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

//MongoDB에서 Transcript 데이터를 읽어오는 Reader
@Component
@RequiredArgsConstructor
public class TranscriptReader {

    private final MongoTemplate batchMongoTemplate;
    private final TranscriptBatchService transcriptBatchService;

    //날짜 기반으로 Transcript 읽기 (startTime 기준)
    public ItemReader<Transcript> createDateBasedReader(String date) {
        return new ItemReader<Transcript>() {
            private Iterator<Transcript> transcriptIterator;
            private boolean initialized = false;

            @Override
            public Transcript read() throws Exception {
                if (!initialized) {
                    LocalDateTime targetDate = date != null ? 
                        LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME) : 
                        LocalDateTime.now().minusDays(1); //기본값: 어제

                    List<Transcript> transcripts = transcriptBatchService.findByStartTimeAfter(targetDate);
                    
                    // 기본적인 유효성 검증을 여기서 수행
                    transcripts = transcripts.stream()
                        .filter(transcript -> isValidTranscript(transcript))
                        .collect(Collectors.toList());
                        
                    transcriptIterator = transcripts.iterator();
                    initialized = true;
                }

                return transcriptIterator.hasNext() ? transcriptIterator.next() : null;
            }
            
            private boolean isValidTranscript(Transcript transcript) {
                return transcript != null && 
                       transcript.getTranscriptId() != null && 
                       transcript.getConversation() != null && 
                       !transcript.getConversation().isEmpty() &&
                       transcript.getConversation().stream()
                           .anyMatch(entry -> entry.getContent() != null && !entry.getContent().trim().isEmpty());
            }
        };
    }

    //ID 기반으로 Transcript 읽기
    public ItemReader<Transcript> createIdBasedReader(List<String> ids) {
        return new ItemReader<Transcript>() {
            private Iterator<Transcript> transcriptIterator;
            private boolean initialized = false;

            @Override
            public Transcript read() throws Exception {
                if (!initialized) {
                    List<Transcript> transcripts = transcriptBatchService.findByIds(ids);
                    
                    // 기본적인 유효성 검증을 여기서 수행
                    transcripts = transcripts.stream()
                        .filter(transcript -> isValidTranscript(transcript))
                        .collect(Collectors.toList());
                        
                    transcriptIterator = transcripts.iterator();
                    initialized = true;
                }

                return transcriptIterator.hasNext() ? transcriptIterator.next() : null;
            }
            
            private boolean isValidTranscript(Transcript transcript) {
                return transcript != null && 
                       transcript.getTranscriptId() != null && 
                       transcript.getConversation() != null && 
                       !transcript.getConversation().isEmpty() &&
                       transcript.getConversation().stream()
                           .anyMatch(entry -> entry.getContent() != null && !entry.getContent().trim().isEmpty());
            }
        };
    }
} 