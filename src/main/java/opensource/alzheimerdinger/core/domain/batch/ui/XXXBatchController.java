package opensource.alzheimerdinger.core.domain.batch.ui;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.batch.application.dto.request.XXXBatchRequest;
import opensource.alzheimerdinger.core.domain.batch.application.dto.response.XXXBatchResponse;
import opensource.alzheimerdinger.core.domain.batch.application.usecase.XXXUseCase;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batch/xxx")
public class XXXBatchController {

    private final XXXUseCase xxxUseCase;

    /** 배치 실행 */
    @PostMapping("/execute")
    public BaseResponse<XXXBatchResponse> executeBatch(@RequestBody @Valid XXXBatchRequest request) {
        if (request.hasTargetIds()) {
            xxxUseCase.executeXXXBatchByIds(request.targetIds());
        } else {
            xxxUseCase.executeXXXBatch(request.date());
        }
        
        return BaseResponse.onSuccess(
            XXXBatchResponse.success("batch-" + System.currentTimeMillis(), 0, null)
        );
    }
} 