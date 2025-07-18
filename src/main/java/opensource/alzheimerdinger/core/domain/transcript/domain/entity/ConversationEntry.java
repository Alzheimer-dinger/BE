package opensource.alzheimerdinger.core.domain.transcript.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ConversationEntry {
    public enum Speaker { patient, ai }

    private Speaker speaker;
    private String content;
}
