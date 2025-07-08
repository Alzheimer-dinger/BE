package opensource.alzheimerdinger.core.domain.user.application.request;

public record SignUpRequest(
        String email,
        String password,
        String role
) {}
