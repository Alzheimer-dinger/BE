package opensource.alzheimerdinger.core.domain.image.infra.storage;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GcsStorageService implements StorageService {


    private final Storage storage;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    @Override
    public String upload(MultipartFile file, String objectName) {
        // 1) 실제 업로드
        String url;
        try {
            BlobId blobId = BlobId.of(bucketName, objectName);
            BlobInfo info = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(info, file.getBytes());
            url = String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
            log.debug("[GCS 업로드 성공] objectName={}, url={}", objectName, url);
        } catch (IOException e) {
            log.error("[파일 읽기 오류] objectName={}, error={}", objectName, e.getMessage(), e);
            throw new RestApiException(_FALIED_READ_FILE);
        } catch (StorageException e) {
            log.error("[GCS 업로드 실패] objectName={}, error={}", objectName, e.getMessage(), e);
            throw new RestApiException(_S3_UPLOAD_ERROR);
        }

        // 2) ACL 설정 (실패해도 업로드 자체는 정상으로 처리)
        try {
            storage.createAcl(BlobId.of(bucketName, objectName),
                    Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
            log.debug("[GCS ACL 설정 성공] objectName={}", objectName);
        } catch (StorageException e) {
            log.warn("[GCS ACL 설정 실패] objectName={}, error={}", objectName, e.getMessage());
        }

        return url;
    }

    @Override
    public void delete(String prefix) {
        Page<Blob> blobs = storage.list(
                bucketName,
                Storage.BlobListOption.prefix(prefix)
        );
        for (Blob blob : blobs.iterateAll()) {
            storage.delete(blob.getBlobId());
            log.debug("[GCS 삭제] objectName={}", blob.getName());
        }
    }
}