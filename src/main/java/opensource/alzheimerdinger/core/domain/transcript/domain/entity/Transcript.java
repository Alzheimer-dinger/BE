package opensource.alzheimerdinger.core.domain.transcript.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;

@Document(collection = "transcripts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transcript {
    @Id
    private String id;
    private String sessionId;
    private int sessionSeq;
    private LocalDate conversationDate;
    private Speaker speaker;
    private String script;
    private Instant timestamp;
}
