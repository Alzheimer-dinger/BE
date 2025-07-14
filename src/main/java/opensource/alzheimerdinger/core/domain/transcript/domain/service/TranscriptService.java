package opensource.alzheimerdinger.core.domain.transcript.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.request.TranscriptRequest;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Speaker;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import opensource.alzheimerdinger.core.domain.transcript.domain.repository.TranscriptRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TranscriptService {
    private final TranscriptRepository repository;
    public Transcript save(TranscriptRequest request) {
        Transcript entity = Transcript.builder()
                .sessionId(request.getSessionId())
                .sessionSeq(request.getSessionSeq())
                .conversationDate(request.getConversationDate())
                .speaker(Speaker.valueOf(request.getSpeaker().toUpperCase()))
                .script(request.getScript())
                .build();
        return repository.save(entity);
    }
}
