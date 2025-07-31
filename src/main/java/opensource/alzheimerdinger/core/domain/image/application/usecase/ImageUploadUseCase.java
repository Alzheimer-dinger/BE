package opensource.alzheimerdinger.core.domain.image.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.application.dto.response.UploadUrlResponse;
import opensource.alzheimerdinger.core.domain.image.domain.service.ImageService;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageUploadUseCase {

    private final ImageService imageService;
    private final UserService userService;

    public UploadUrlResponse requestPostUrl(String userId, String extension) {
        return imageService.requestUploadUrl(userId, extension);
    }

    public ProfileResponse updateImage(String userId, String fileKey) {
        String imageUrl = imageService.updateProfileImage(userId, fileKey);
        ProfileResponse profile = userService.findProfile(userId);
        return new ProfileResponse(
                profile.userId(),
                profile.name(),
                profile.email(),
                profile.gender(),
                imageUrl
        );
    }
}