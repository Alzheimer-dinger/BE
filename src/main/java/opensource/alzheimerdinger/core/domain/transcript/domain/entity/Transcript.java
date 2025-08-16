package opensource.alzheimerdinger.core.domain.transcript.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transcripts")
public class Transcript {

    // 세션 ID를 도큐먼트의 기본 키로 사용
	@Id
    @Field("session_id")
    private String sessionId;

    @Indexed
    @Field("user_id")
    private String userId;

    @Field("start_time")
    private Instant startTime;

    @Field("end_time")
    private Instant endTime;

    private List<TranscriptMessage> conversation;

    private String summary;
}


