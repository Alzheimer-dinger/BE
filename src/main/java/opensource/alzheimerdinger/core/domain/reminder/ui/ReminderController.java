package opensource.alzheimerdinger.core.domain.reminder.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary     = "리마인더 등록",
            description = "사용자가 설정한 리마인더를 등록합니다.",
            responses   = @ApiResponse(
                    responseCode = "200",
                    description  = "등록 성공"
            )
    )
    @PostMapping
    public BaseResponse<Void> register(
            @Parameter(hidden = true)
            @CurrentUser String userId, @RequestBody @Valid ReminderSettingRequest request) {
        reminderCommandUseCase.register(userId, request);
        return BaseResponse.onSuccess();
    }

    @Operation(
            summary     = "리마인더 조회",
            description = "등록된 리마인더 정보를 조회합니다.",
            responses   = @ApiResponse(
                    responseCode = "200",
                    description  = "조회 성공",
                    content      = @Content(schema = @Schema(implementation = ReminderResponse.class))
            )
    )
    @GetMapping
    public BaseResponse<ReminderResponse> find(
            @Parameter(hidden = true)
            @CurrentUser String userId) {
        return BaseResponse.onSuccess(reminderQueryUseCase.find(userId));
    }
}
