package opensource.alzheimerdinger.core.domain.image.infra.storage;

import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GcsStorageService implements StorageService {
    private final Storage storage;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    @Override
    public String generateUploadUrl(String objectName) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();
        URL url = storage.signUrl(
                blobInfo,
                30, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withV4Signature()
        );
        log.debug("[Presigned PUT URL] objectName={}, url={}", objectName, url);
        return url.toString();
    }

    @Override
    public String generateSignedGetUrl(String objectName, long minutesToLive) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName).build();
        URL url = storage.signUrl(
                blobInfo,
                minutesToLive, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                Storage.SignUrlOption.withV4Signature()
        );
        log.debug("[Presigned GET URL] object={}, ttlMin={}, url={}", objectName, minutesToLive, url);
        return url.toString();
    }

    @Override
    public String getPublicUrl(String objectName) {
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
    }
}