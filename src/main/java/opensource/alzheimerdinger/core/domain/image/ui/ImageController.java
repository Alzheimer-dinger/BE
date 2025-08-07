package opensource.alzheimerdinger.core.domain.image.ui;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.application.dto.request.UpdateProfileImageRequest;
import opensource.alzheimerdinger.core.domain.image.application.dto.response.UploadUrlResponse;
import opensource.alzheimerdinger.core.domain.image.application.usecase.ImageUploadUseCase;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final ImageUploadUseCase useCase;

    @GetMapping("/profile/upload-url")
    public UploadUrlResponse requestPostUrl(
            @Parameter(hidden = true)
            @CurrentUser String userId, @RequestParam String extension) {
        return useCase.requestPostUrl(userId, extension);
    }

    @PostMapping("/profile")
    public ProfileResponse updateImage(
            @Parameter(hidden = true)
            @CurrentUser String userId, @RequestBody @Valid UpdateProfileImageRequest req) {
        return useCase.updateImage(userId, req.fileKey());
    }
}