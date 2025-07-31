package opensource.alzheimerdinger.core.domain.image.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.application.dto.response.UploadUrlResponse;
import opensource.alzheimerdinger.core.domain.image.domain.entity.ProfileImage;
import opensource.alzheimerdinger.core.domain.image.domain.repository.ProfileImageRepository;
import opensource.alzheimerdinger.core.domain.image.infra.storage.StorageService;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.repository.UserRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Transactional(readOnly = true)
    public UploadUrlResponse requestUploadUrl(String userId, String extension) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));

        String fileKey = String.format("images/%s/%s.%s",
                userId, UUID.randomUUID(), extension);
        String uploadUrl = storageService.generateUploadUrl(fileKey);
        return new UploadUrlResponse(fileKey, uploadUrl);
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