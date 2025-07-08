package opensource.alzheimerdinger.core.global.exception;

import lombok.AllArgsConstructor;
import opensource.alzheimerdinger.core.global.exception.code.BaseCode;
import opensource.alzheimerdinger.core.global.exception.code.BaseCodeInterface;

@AllArgsConstructor // 생성자를 만들어주는 어노테이션
public class RestApiException extends RuntimeException {

    private final BaseCodeInterface errorCode; //추상화 시킨 인터페이스를 받아서 사용

    //추상화 시킨 ErrorCode의 getrCode()를 사용하여 ErrorCode를 반환
    public BaseCode getErrorCode() {
        return this.errorCode.getCode();
    }
}
