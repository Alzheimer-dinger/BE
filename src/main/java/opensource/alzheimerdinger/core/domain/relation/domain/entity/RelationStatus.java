package opensource.alzheimerdinger.core.domain.relation.domain.entity;

public enum RelationStatus {
    REQUESTED,        // 요청 보냄/받음
    ACCEPTED,      // 요청 수락 후 연결됨
    REJECTED,         // 요청 거절
    DISCONNECTED,    // 연결 해제
}
