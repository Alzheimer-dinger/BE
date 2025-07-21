package opensource.alzheimerdinger.core.domain.transcript.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "transcripts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transcript {
    @Id
    private String transcriptId;
    private String sessionId;
    private String userId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<ConversationEntry> conversation;
    private List<SummaryEntry> summary;
}
