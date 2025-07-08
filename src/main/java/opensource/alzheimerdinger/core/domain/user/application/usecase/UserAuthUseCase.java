package opensource.alzheimerdinger.core.domain.user.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.application.request.SignUpRequest;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.stereotype.Service;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus._EXIST_ENTITY;

@Service
@RequiredArgsConstructor
public class UserAuthUseCase {

    private final UserService userService;

    public void signUp(SignUpRequest request) {
        if (userService.isAlreadyRegistered(request.email()))
            throw new RestApiException(_EXIST_ENTITY);

        userService.signUp(request);
    }
}
