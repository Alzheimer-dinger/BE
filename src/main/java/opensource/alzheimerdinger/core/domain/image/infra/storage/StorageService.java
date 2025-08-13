package opensource.alzheimerdinger.core.domain.image.infra.storage;


public interface StorageService {
    /**
     * presigned PUT URL 생성
     */
    String generateUploadUrl(String objectName);

    // (서명에 Content-Type 포함)
    String generateUploadUrl(String objectName, String contentType);

    /**
     * public 버킷인 경우 파일에 접근할 수 있는 URL
     */
    String getPublicUrl(String objectName);
}