package opensource.alzheimerdinger.core.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

@Configuration
public class GcsConfig {

    /**
     * GCP 서비스 계정 키(JSON) 파일 경로 (classpath:…)
     */
    @Value("${gcp.credentials.file}")
    private Resource credentialsResource;

    /**
     * GCP 프로젝트 ID
     */
    @Value("${gcp.project-id}")
    private String projectId;

    /**
     * Google Cloud Storage 클라이언트 빈
     */
    @Bean
    public Storage googleStorage() throws IOException {
        GoogleCredentials creds = GoogleCredentials
                .fromStream(credentialsResource.getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        return StorageOptions.newBuilder()
                .setCredentials(creds)
                .setProjectId(projectId)
                .build()
                .getService();
    }
}