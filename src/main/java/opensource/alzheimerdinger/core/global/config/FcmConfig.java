package opensource.alzheimerdinger.core.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.global.config.properties.FcmProperties;
import opensource.alzheimerdinger.core.global.exception.RestApiException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FcmConfig {

    private final FcmProperties fcmProperties;

    @PostConstruct
    public void init() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new FileInputStream(fcmProperties.getPrefix() + fcmProperties.getFirebaseConfigPath()));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(googleCredentials)
                    .build();

            if(FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RestApiException(null);
        }
    }
}
