package opensource.alzheimerdinger.core.domain.transcript.application.dto.response;

import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Speaker;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record TranscriptDetailResponse(
	String sessionId,
	String title,
	LocalDate date,
	LocalTime startTime,
	LocalTime endTime,
	String durationSeconds,
	String summary,
	List<Message> conversation
) {
	public record Message(
		Speaker speaker,
		String content
	) {}
}
