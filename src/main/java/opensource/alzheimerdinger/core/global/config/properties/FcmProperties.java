package opensource.alzheimerdinger.core.global.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties("fcm")
public class FcmProperties {

    private String prefix;
    private String firebaseConfigPath;

}
