package opensource.alzheimerdinger.core.domain.transcript.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptDetailResponse;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptListResponse;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.TranscriptMessage;
import opensource.alzheimerdinger.core.domain.transcript.domain.repository.TranscriptRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TranscriptService {

    private final TranscriptRepository transcriptRepository;

    public List<TranscriptListResponse> getList(String userId) {
        List<Transcript> transcripts = transcriptRepository.findByUser(userId);

        ZoneId zoneId = ZoneId.systemDefault();
        return transcripts.stream()
                .map(t -> new TranscriptListResponse(
                        t.getSessionId(),
                        t.getTitle(),
                        LocalDate.ofInstant(t.getStartTime(), zoneId),
                        LocalTime.ofInstant(t.getStartTime(), zoneId),
                        LocalTime.ofInstant(t.getEndTime(), zoneId),
                        String.valueOf(Duration.between(t.getStartTime(), t.getEndTime()).toSeconds())
                ))
                .toList();
    }

    public TranscriptDetailResponse getDetail(String userId, String sessionId) {
        Transcript transcript = transcriptRepository.findBySessionId((sessionId))
                .filter(t -> t.getUserId().equals(userId))
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));

        ZoneId zoneId = ZoneId.systemDefault();

        List<TranscriptDetailResponse.Message> messages = transcript.getConversation() == null
                ? List.of()
                : transcript.getConversation().stream()
                .map(this::mapMessage)
                .collect(Collectors.toList());

        return new TranscriptDetailResponse(
                transcript.getSessionId(),
                transcript.getTitle(),
                LocalDate.ofInstant(transcript.getStartTime(), zoneId),
                LocalTime.ofInstant(transcript.getStartTime(), zoneId),
                LocalTime.ofInstant(transcript.getEndTime(), zoneId),
                String.valueOf(Duration.between(transcript.getStartTime(), transcript.getEndTime()).toSeconds()),
                transcript.getSummary(),
                messages
        );
    }

    private TranscriptDetailResponse.Message mapMessage(TranscriptMessage message) {
        return new TranscriptDetailResponse.Message(
                message.getSpeaker(),
                message.getContent()
        );
    }

    private String buildTitle(Transcript transcript) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate date = LocalDate.ofInstant(transcript.getStartTime(), zoneId);
        LocalTime time = LocalTime.ofInstant(transcript.getStartTime(), zoneId);
        return String.format("%s %02d:%02d 통화", date, time.getHour(), time.getMinute());
    }
}
