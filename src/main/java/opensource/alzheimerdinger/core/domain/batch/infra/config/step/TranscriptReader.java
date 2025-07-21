package opensource.alzheimerdinger.core.domain.batch.infra.config.step;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.batch.domain.service.TranscriptBatchService;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

//MongoDB에서 모든 유저의 Transcript 데이터를 읽어오는 Reader
@Component
@RequiredArgsConstructor
public class TranscriptReader {

    private final TranscriptBatchService transcriptBatchService;

    // 모든 유저 기간 기반 Transcript 읽기
    public ItemReader<Transcript> createAllUsersReader(String fromDate, String toDate) {
        return new ItemReader<Transcript>() {
            private Iterator<Transcript> transcriptIterator;
            private boolean initialized = false;

            @Override
            public Transcript read() throws Exception {
                if (!initialized) {
                    LocalDateTime fromDateTime = LocalDateTime.parse(fromDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    LocalDateTime toDateTime = LocalDateTime.parse(toDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    
                    // 모든 유저 데이터 조회
                    List<Transcript> transcripts = transcriptBatchService.findByPeriod(fromDateTime, toDateTime);
                    
                    // 기본적인 유효성 검증
                    transcripts = transcripts.stream()
                        .filter(transcript -> isValidTranscript(transcript))
                        .collect(Collectors.toList());
                        
                    transcriptIterator = transcripts.iterator();
                    initialized = true;
                }

                return transcriptIterator.hasNext() ? transcriptIterator.next() : null;
            }
        };
    }

    private boolean isValidTranscript(Transcript transcript) {
        return transcript != null && 
               transcript.getTranscriptId() != null && 
               transcript.getConversation() != null && 
               !transcript.getConversation().isEmpty() &&
               transcript.getConversation().stream()
                   .anyMatch(entry -> entry.getContent() != null && !entry.getContent().trim().isEmpty());
    }
} 