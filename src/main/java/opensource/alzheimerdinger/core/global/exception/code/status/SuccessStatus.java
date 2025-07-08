package opensource.alzheimerdinger.core.global.exception.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import opensource.alzheimerdinger.core.global.exception.code.BaseCode;
import opensource.alzheimerdinger.core.global.exception.code.BaseCodeInterface;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus  implements BaseCodeInterface {
    // For test
    _OK(HttpStatus.OK, "COMMON200", "성공입니다.")
    ;

    private final HttpStatus httpStatus;
    private final boolean isSuccess = true;
    private final String code;
    private final String message;

    @Override
    public BaseCode getCode() {
        return BaseCode.builder()
                .httpStatus(httpStatus)
                .isSuccess(isSuccess)
                .code(code)
                .message(message)
                .build();
    }
}
