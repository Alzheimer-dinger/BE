package opensource.alzheimerdinger.core.domain.transcript.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.request.TranscriptRequest;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptResponse;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import opensource.alzheimerdinger.core.domain.transcript.domain.service.TranscriptService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SaveTranscriptUseCase {
    private final TranscriptService transcriptService;

    public TranscriptResponse execute(TranscriptRequest request) {
        Transcript saved = transcriptService.save(request);
        TranscriptResponse resp = new TranscriptResponse();
        resp.setId(saved.getId());
        resp.setSessionId(saved.getSessionId());
        resp.setSpeaker(saved.getSpeaker().name());
        resp.setScript(saved.getScript());
        resp.setTimestamp(saved.getTimestamp().toString());
        return resp;
    }
}
