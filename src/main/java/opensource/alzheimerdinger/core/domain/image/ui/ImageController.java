package opensource.alzheimerdinger.core.domain.image.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.application.dto.request.UpdateProfileImageRequest;
import opensource.alzheimerdinger.core.domain.image.application.dto.response.UploadUrlResponse;
import opensource.alzheimerdinger.core.domain.image.application.usecase.ImageUploadUseCase;
import opensource.alzheimerdinger.core.domain.user.application.dto.response.ProfileResponse;
import opensource.alzheimerdinger.core.global.annotation.CurrentUser;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Image", description = "프로필 이미지 업로드 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final ImageUploadUseCase useCase;

    @Operation(
            summary     = "프로필 업로드 URL 요청",
            description = "현재 사용자의 프로필 이미지 업로드를 위한 presigned URL을 발급받습니다.",
            responses   = @ApiResponse(
                    responseCode = "200",
                    description  = "URL 발급 성공",
                    content      = @Content(schema = @Schema(implementation = UploadUrlResponse.class))
            )
    )
    @GetMapping("/profile/upload-url")
    public UploadUrlResponse requestPostUrl(
            @Parameter(hidden = true)
            @CurrentUser String userId, @RequestParam String extension) {
        return useCase.requestPostUrl(userId, extension);
    }

    @Operation(
            summary     = "프로필 이미지 업데이트",
            description = "발급받은 presigned URL로 업로드된 파일을 프로필 이미지로 설정합니다.",
            responses   = @ApiResponse(
                    responseCode = "200",
                    description  = "프로필 이미지 업데이트 성공",
                    content      = @Content(schema = @Schema(implementation = ProfileResponse.class))
            )
    )
    @PostMapping("/profile")
    public ProfileResponse updateImage(
            @Parameter(hidden = true)
            @CurrentUser String userId, @RequestBody @Valid UpdateProfileImageRequest req) {
        return useCase.updateImage(userId, req.fileKey());
    }
}