package opensource.alzheimerdinger.core.domain.transcript.application.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptDetailResponse;
import opensource.alzheimerdinger.core.domain.transcript.application.dto.response.TranscriptListResponse;
import opensource.alzheimerdinger.core.domain.transcript.domain.service.TranscriptService;
import opensource.alzheimerdinger.core.global.metric.UseCaseMetric;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TranscriptUseCase {

    private final TranscriptService transcriptService;

    @UseCaseMetric(domain = "transcript", value = "list", type = "query")
    public List<TranscriptListResponse> list(String userId) {
        return transcriptService.getList(userId);
    }

    @UseCaseMetric(domain = "transcript", value = "detail", type = "query")
    public TranscriptDetailResponse detail(String userId, String sessionId) {
        return transcriptService.getDetail(userId, sessionId);
    }
}
