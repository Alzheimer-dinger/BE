package opensource.alzheimerdinger.core;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import opensource.alzheimerdinger.core.global.config.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@OpenAPIDefinition(
        info = @Info(
                title       = "AlzheimerDinger API",
                version     = "1.0.0",
                description = "알츠하이머 분석 서비스 API 명세"
        )
)
@EnableConfigurationProperties({
        CorsProperties.class
})
@SpringBootApplication
@ConfigurationPropertiesScan
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}
