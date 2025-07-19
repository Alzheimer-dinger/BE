package opensource.alzheimerdinger.core.domain.transcript.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.request.TranscriptRequest;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import opensource.alzheimerdinger.core.domain.transcript.domain.repository.TranscriptRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TranscriptService {
    private final TranscriptRepository repository;
    public Transcript save(TranscriptRequest request) {
        Transcript entity = Transcript.builder()
                .transcriptId(UUID.randomUUID().toString())
                .sessionId(request.getSessionId())
                .userId(request.getUserId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .conversation(request.getConversation())
                .build();
        return repository.save(entity);
    }
}
