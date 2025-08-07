package opensource.alzheimerdinger.core.domain.user.application.dto.response;

public record TokenReissueResponse (
        String accessToken,
        String refreshToken
) {}
