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
    private final UserRepository userRepository;

    @Value("${gcp.storage.default-profile-url}")
    private String defaultProfileUrl;

    /**
     * presigned URL만 반환
     */
    @Transactional(readOnly = true)
    public String requestUploadUrl(String userId, String extension) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));

        String fileKey = String.format("images/%s/%s.%s",
                userId, UUID.randomUUID(), extension);
        return storageService.generateUploadUrl(fileKey);
    }

    @Transactional
    public String updateProfileImage(String userId, String fileKey) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));

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

    @Transactional(readOnly = true)
    public String getProfileImageUrl(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));

        return imageRepo.findByUser(user)
                .map(ProfileImage::getFileKey)
                .map(storageService::getPublicUrl)
                .orElse(defaultProfileUrl);
    }
}