package opensource.alzheimerdinger.core.domain.image.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.application.dto.response.UploadUrlResponse;
import opensource.alzheimerdinger.core.domain.image.domain.service.ImageService;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import opensource.alzheimerdinger.core.global.metric.UseCaseMetric;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageUploadUseCase {

    private final ImageService imageService;
    private final UserService userService;

    /** presigned URL 요청 */
    @UseCaseMetric(domain = "image", value = "request-upload-url", type = "command")
    public UploadUrlResponse requestPostUrl(String userId, String extension) {
        User user = userService.findUser(userId);
        String uploadUrl = imageService.requestUploadUrl(user, extension);
        return new UploadUrlResponse(uploadUrl);
    }

    /** fileKey 저장 및 ProfileResponse 반환 */
    @UseCaseMetric(domain = "image", value = "update-profile-image", type = "command")
    public ProfileResponse updateImage(String userId, String fileKey) {
        User user = userService.findUser(userId);
        String imageUrl = imageService.updateProfileImage(user, fileKey);
        var profile = userService.findProfile(userId);
        return new ProfileResponse(
                profile.userId(),
                profile.name(),
                profile.email(),
                profile.gender(),
                imageUrl
        );
    }
}