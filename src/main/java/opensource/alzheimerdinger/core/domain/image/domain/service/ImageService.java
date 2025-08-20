package opensource.alzheimerdinger.core.domain.image.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.domain.entity.ProfileImage;
import opensource.alzheimerdinger.core.domain.image.domain.repository.ProfileImageRepository;
import opensource.alzheimerdinger.core.domain.image.infra.storage.StorageService;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.repository.UserRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus.*;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ProfileImageRepository imageRepo;
    private final StorageService storageService;

    @Value("${gcp.storage.default-profile-url}")
    private String defaultProfileUrl;

    /**
     * presigned URL 반환; User 엔티티는 UseCase에서 조회
     */
    @Transactional(readOnly = true)
    public String requestUploadUrl(User user, String extension) {
        String fileKey = String.format("images/%s/%s.%s",
                user.getUserId(), java.util.UUID.randomUUID(), extension);
        return storageService.generateUploadUrl(fileKey);
    }

    /**
     * fileKey 저장 및 public URL 반환
     */
    @Transactional
    public String updateProfileImage(User user, String fileKey) {
        // 기존 key 확보
        String oldKey = imageRepo.findByUser(user)
                .map(ProfileImage::getFileKey)
                .orElse(null);

        // DB 업데이트 (있으면 교체, 없으면 생성)
        imageRepo.findByUser(user).ifPresentOrElse(existing -> {
            existing.updateFileKey(fileKey);
        }, () -> {
            ProfileImage img = ProfileImage.builder()
                    .user(user)
                    .fileKey(fileKey)
                    .build();
            imageRepo.save(img);
        });

        // 커밋 후 이전 Blob 삭제 (최신 1개만 유지)
        if (oldKey != null && !oldKey.equals(fileKey)) {
            final String toDelete = oldKey;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    storageService.deleteObject(toDelete);
                }
            });
        }

        // 보기용 URL 반환 - 서명 URL 24시간
        return storageService.generateSignedGetUrl(fileKey, 60 * 24);
    }

    /**
     * 저장된 fileKey → public URL 또는 default URL 반환
     */
    @Transactional(readOnly = true)
    public String getProfileImageUrl(User user) {
        return imageRepo.findByUser(user)
                .map(ProfileImage::getFileKey)
                .map(key -> storageService.generateSignedGetUrl(key, 60 * 24))
                .orElse(defaultProfileUrl);
    }
}