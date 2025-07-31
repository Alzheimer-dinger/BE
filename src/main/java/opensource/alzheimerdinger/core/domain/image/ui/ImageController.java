package opensource.alzheimerdinger.core.domain.image.ui;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.application.usecase.ImageUploadUseCase;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageUploadUseCase imageUploadUseCase;

    @PostMapping(
            value = "/profile/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ProfileResponse uploadImage(@CurrentUser String userId, @RequestPart("file") MultipartFile file) {
        return imageUploadUseCase.uploadProfileImage(userId, file);
    }
}
