package opensource.alzheimerdinger.core.domain.user.application.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
