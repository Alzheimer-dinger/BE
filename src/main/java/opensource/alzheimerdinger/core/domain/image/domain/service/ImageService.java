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
     * presigned URL 생성 (fileKey는 UseCase에서 생성)
     */
    @Transactional(readOnly = true)
    public String generateUploadUrl(String fileKey) {
        return storageService.generateUploadUrl(fileKey);
    }

    /**
     * Content-Type을 서명에 포함하는 버전
     */
    @Transactional(readOnly = true)
    public String generateUploadUrl(String fileKey, String contentType) {
        return storageService.generateUploadUrl(fileKey, contentType);
    }


    /**
     * fileKey 저장 및 public URL 반환
     */
    @Transactional
    public String updateProfileImage(User user, String fileKey) {
        imageRepo.findByUser(user).ifPresentOrElse(existing -> {
            existing.updateFileKey(fileKey);
        }, () -> {
            ProfileImage img = ProfileImage.builder()
                    .user(user)
                    .fileKey(fileKey)
                    .build();
            imageRepo.save(img);
        });
        return storageService.getPublicUrl(fileKey);
    }

    /**
     * 저장된 fileKey → public URL 또는 default URL 반환
     */
    @Transactional(readOnly = true)
    public String getProfileImageUrl(User user) {
        return imageRepo.findByUser(user)
                .map(ProfileImage::getFileKey)
                .map(storageService::getPublicUrl)
                .orElse(defaultProfileUrl);
    }
}