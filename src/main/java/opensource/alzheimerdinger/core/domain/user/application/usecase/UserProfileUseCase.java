package opensource.alzheimerdinger.core.domain.user.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.domain.service.ImageService;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileUseCase {

    private final UserService userService;
    private final ImageService imageService;

    public ProfileResponse findProfile(String userId) {
        var userDto   = userService.findProfile(userId);
        String imageUrl = imageService.getProfileImageUrl(userId);

        return new ProfileResponse(
                userDto.userId(),
                userDto.name(),
                userDto.email(),
                userDto.gender(),
                imageUrl
        );
    }
}