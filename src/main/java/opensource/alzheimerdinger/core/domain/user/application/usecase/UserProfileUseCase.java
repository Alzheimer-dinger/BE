package opensource.alzheimerdinger.core.domain.user.application.usecase;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.domain.service.ImageService;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.domain.user.domain.service.UserService;
import opensource.alzheimerdinger.core.global.metric.UseCaseMetric;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileUseCase {

    private final UserService userService;
    private final ImageService imageService;

    @UseCaseMetric(domain = "user-profile", value = "find-profile", type = "query")
    public ProfileResponse findProfile(String userId) {
        ProfileResponse profileDto = userService.findProfile(userId);
        User user = userService.findUser(userId);
        String imageUrl = imageService.getProfileImageUrl(user);

        return new ProfileResponse(
                profileDto.userId(),
                profileDto.name(),
                profileDto.email(),
                profileDto.gender(),
                imageUrl,
                profileDto.patientCode()
        );
    }
}