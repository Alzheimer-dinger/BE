package opensource.alzheimerdinger.core.domain.image.domain.service;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.image.domain.entity.ProfileImage;
import opensource.alzheimerdinger.core.domain.image.domain.repository.ProfileImageRepository;
import opensource.alzheimerdinger.core.domain.image.infra.storage.StorageService;
import opensource.alzheimerdinger.core.domain.user.domain.repository.UserRepository;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static opensource.alzheimerdinger.core.global.exception.code.status.GlobalErrorStatus.*;

@Service
@RequiredArgsConstructor
public class ImageService {
    private static final Logger log = LoggerFactory.getLogger(ImageService.class);

    private final ProfileImageRepository imageRepo;
    private final StorageService storageService;
    private final UserRepository userRepository;

    @Value("${gcp.storage.default-profile-url}")
    private String defaultProfileUrl;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    /**
     * 프로필 이미지 GCS 업로드 후, URL을 DB에 저장(삽입 또는 수정)하고 반환
     */
    @Transactional
    public String uploadProfileImage(String userId, MultipartFile file) {
        log.debug("uploadProfileImage 시작: userId={}, fileName={}", userId, file.getOriginalFilename());

        // 1) 사용자 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(_NOT_FOUND));

        // GCS 업로드
        String ext        = FilenameUtils.getExtension(file.getOriginalFilename());
        String objectName = String.format("image/%s/%s.%s", userId, UUID.randomUUID(), ext);
        String url        = storageService.upload(file, objectName);
        log.debug("GCS 업로드 성공: url={}", url);

        // DB 처리: 존재하면 업데이트, 아니면 새로 삽입
        imageRepo.findByUserId(userId).ifPresentOrElse(existing -> {
            // (선택) 이전 GCS 오브젝트 삭제
            String prefix        = "https://storage.googleapis.com/" + bucketName + "/";
            String oldObjectName = existing.getImageUrl().replace(prefix, "");
            try {
                storageService.delete(oldObjectName);
                log.debug("이전 GCS 오브젝트 삭제 성공: {}", oldObjectName);
            } catch (Exception e) {
                log.warn("이전 GCS 삭제 실패: {}", e.getMessage());
            }

            // Dirty Checking 으로 URL만 갱신
            existing.updateImageUrl(url);
            log.debug("기존 DB 레코드 URL 갱신: imageId={}, newUrl={}", existing.getImageId(), url);

        }, () -> {
            // 신규 레코드 삽입
            ProfileImage img = ProfileImage.builder()
                    .userId(userId)
                    .imageUrl(url)
                    .build();
            imageRepo.save(img);
            log.debug("새로운 DB 레코드 삽입: imageId={}, userId={}", img.getImageId(), userId);
        });

        return url;
    }

    /**
     * DB에 저장된 URL 조회, 없으면 기본 URL 반환
     */
    @Transactional(readOnly = true)
    public String getProfileImageUrl(String userId) {
        return imageRepo.findByUserId(userId)
                .map(ProfileImage::getImageUrl)
                .orElse(defaultProfileUrl);
    }
}

