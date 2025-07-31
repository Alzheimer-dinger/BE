package opensource.alzheimerdinger.core.domain.image.infra.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    /**
     * @param file       업로드할 파일
     * @param objectName 버킷 내 저장 경로
     * @return 외부에서 접근 가능한 URL
     */
    String upload(MultipartFile file, String objectName);
    void delete(String objectName);
}
