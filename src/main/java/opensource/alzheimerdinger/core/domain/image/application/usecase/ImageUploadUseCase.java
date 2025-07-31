package opensource.alzheimerdinger.core.domain.image.application.usecase;

import lombok.RequiredArgsConstructor;
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

    /**
     * 이미지 업로드 → 최신 ProfileResponse에 URL 채워서 반환
     */
    public ProfileResponse uploadProfileImage(String userId, MultipartFile file) {
        String imageUrl = imageService.uploadProfileImage(userId, file);
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