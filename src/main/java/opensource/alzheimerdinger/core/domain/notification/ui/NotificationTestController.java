package opensource.alzheimerdinger.core.domain.notification.ui;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.notification.application.usecase.SendNotificationUseCase;
import opensource.alzheimerdinger.core.global.common.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/notification")
public class NotificationTestController {

    private final SendNotificationUseCase sendNotificationUseCase;

    @PostMapping("/send")
    public BaseResponse<Void> send() {
        sendNotificationUseCase.send("token", "content");
        return BaseResponse.onSuccess();
    }
}
