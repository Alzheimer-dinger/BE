package opensource.alzheimerdinger.core.domain.transcript.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record TranscriptListResponse(
	String sessionId,
	String title,
	LocalDate date,
	LocalTime startTime,
	LocalTime endTime,
	String durationSeconds
) {}
