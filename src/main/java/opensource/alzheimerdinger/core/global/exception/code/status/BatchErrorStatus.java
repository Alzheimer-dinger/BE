package opensource.alzheimerdinger.core.global.exception.code.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import opensource.alzheimerdinger.core.global.exception.code.BaseCode;
import opensource.alzheimerdinger.core.global.exception.code.BaseCodeInterface;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BatchErrorStatus implements BaseCodeInterface {

    // 배치 실행 관련 에러
    BATCH_JOB_EXECUTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH001", "배치 작업 실행에 실패했습니다."),
    BATCH_JOB_ALREADY_RUNNING(HttpStatus.CONFLICT, "BATCH002", "동일한 배치 작업이 이미 실행 중입니다."),
    
    // 요청 파라미터 검증 에러
    BATCH_EMPTY_REQUEST_PARAMS(HttpStatus.BAD_REQUEST, "BATCH003", "시작 날짜(fromDate)와 종료 날짜(toDate)는 필수입니다."),
    BATCH_INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "BATCH004", "날짜 형식이 올바르지 않습니다. ISO_LOCAL_DATE_TIME 형식을 사용해주세요."),
    BATCH_INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "BATCH005", "시작 날짜는 종료 날짜보다 이전이어야 합니다."),
    BATCH_INVALID_USER_ID(HttpStatus.BAD_REQUEST, "BATCH006", "유저 ID가 유효하지 않습니다. 빈 문자열은 허용되지 않습니다."),
    BATCH_DATE_RANGE_TOO_LARGE(HttpStatus.BAD_REQUEST, "BATCH007", "날짜 범위가 너무 큽니다. 최대 31일까지만 허용됩니다."),
    
    // 데이터 처리 관련 에러
    BATCH_DATA_READ_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH101", "데이터 읽기에 실패했습니다."),
    BATCH_DATA_PROCESS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH102", "데이터 처리에 실패했습니다."),
    BATCH_DATA_WRITE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH103", "데이터 쓰기에 실패했습니다."),
    BATCH_INVALID_TRANSCRIPT_DATA(HttpStatus.BAD_REQUEST, "BATCH104", "유효하지 않은 Transcript 데이터입니다."),
    BATCH_NO_DATA_FOUND(HttpStatus.NOT_FOUND, "BATCH105", "해당 조건에 맞는 처리할 데이터가 없습니다."),
    
    // Kafka 관련 에러
    BATCH_KAFKA_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH201", "Kafka 메시지 전송에 실패했습니다."),
    BATCH_KAFKA_TOPIC_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH202", "Kafka 토픽을 찾을 수 없습니다."),
    BATCH_KAFKA_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH203", "메시지 직렬화에 실패했습니다."),
    BATCH_KAFKA_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH204", "Kafka 연결에 실패했습니다."),
    
    // MongoDB 관련 에러
    BATCH_MONGO_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH301", "MongoDB 연결에 실패했습니다."),
    BATCH_MONGO_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "BATCH302", "MongoDB 쿼리 실행에 실패했습니다."),
    BATCH_TRANSCRIPT_NOT_FOUND(HttpStatus.NOT_FOUND, "BATCH303", "해당 조건의 Transcript를 찾을 수 없습니다."),
    BATCH_MONGO_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "BATCH304", "MongoDB 쿼리 실행 시간이 초과되었습니다.");

    private final HttpStatus httpStatus;
    private final boolean isSuccess = false;
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