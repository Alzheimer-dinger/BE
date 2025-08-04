package opensource.alzheimerdinger.core.domain.user.application.dto.response;

import opensource.alzheimerdinger.core.domain.user.domain.entity.Gender;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;

public record ProfileResponse(
        String userId,
        String name,
        String email,
        Gender gender,
        String imageUrl
) {
    public static ProfileResponse create(User user) {
        return new ProfileResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getGender(),
                null
        );
    }
}