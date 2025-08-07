package opensource.alzheimerdinger.core.domain.reminder.ui;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.reminder.application.dto.request.ReminderSettingRequest;
import opensource.alzheimerdinger.core.domain.reminder.application.dto.response.ReminderResponse;
import opensource.alzheimerdinger.core.domain.reminder.application.usecase.ReminderCommandUseCase;
import opensource.alzheimerdinger.core.domain.reminder.application.usecase.ReminderQueryUseCase;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reminder")
public class ReminderController {

    private final ReminderCommandUseCase reminderCommandUseCase;
    private final ReminderQueryUseCase reminderQueryUseCase;

    @PostMapping
    public BaseResponse<Void> register(
            @Parameter(hidden = true)
            @CurrentUser String userId, @RequestBody @Valid ReminderSettingRequest request) {
        reminderCommandUseCase.register(userId, request);
        return BaseResponse.onSuccess();
    }

    @GetMapping
    public BaseResponse<ReminderResponse> find(
            @Parameter(hidden = true)
            @CurrentUser String userId) {
        return BaseResponse.onSuccess(reminderQueryUseCase.find(userId));
    }
}
