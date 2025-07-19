package opensource.alzheimerdinger.core.global.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.URI;

@Configuration
@EnableMongoRepositories(basePackages = {
    "opensource.alzheimerdinger.core.domain.transcript.domain.repository",
    "opensource.alzheimerdinger.core.domain.batch.infra.repository"
    // 향후 다른 MongoDB repository 패키지들도 여기에 추가
})
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        try {
            URI uri = new URI(mongoUri);
            String path = uri.getPath();
            if (path != null && path.length() > 1) {
                // URI path에서 데이터베이스 이름 추출 (맨 앞의 '/' 제거)
                return path.substring(1);
            }
        } catch (Exception e) {
            // URI 파싱 실패 시 기본값 사용
        }
        return "alzheimerdinger"; // 기본 데이터베이스 이름
    }

    @Bean
    @Primary  // 기본 MongoClient로 설정
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    @Bean
    @Primary  // 기본 MongoTemplate로 설정
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }

    // 배치용 MongoTemplate (기존 코드 호환성을 위해 별칭으로 유지)
    @Bean("batchMongoTemplate")
    public MongoTemplate batchMongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }

    // 배치용 MongoClient (기존 코드 호환성을 위해 별칭으로 유지)
    @Bean("batchMongoClient")
    public MongoClient batchMongoClient() {
        return mongoClient();
    }

    @Override
    protected boolean autoIndexCreation() {
        return true; // 인덱스 자동 생성
    }
}