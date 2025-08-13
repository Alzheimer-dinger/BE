package opensource.alzheimerdinger.core.domain.transcript.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptMessage {

    private Speaker speaker;

    private String content;
}


