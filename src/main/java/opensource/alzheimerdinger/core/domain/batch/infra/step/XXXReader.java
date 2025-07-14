package opensource.alzheimerdinger.core.domain.batch.infra.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.domain.entity.XXXEntity;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class XXXReader {

    private final EntityManagerFactory entityManagerFactory;

    public ItemReader<XXXEntity> createDateBasedReader(LocalDateTime date) {
        return new JpaPagingItemReaderBuilder<XXXEntity>()
                .name("xxxReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString("SELECT x FROM XXXEntity x WHERE x.createdDate > :date ORDER BY x.id")
                .parameterValues(java.util.Map.of("date", date))
                .build();
    }

    public ItemReader<XXXEntity> createStatusBasedReader(String status) {
        return new JpaPagingItemReaderBuilder<XXXEntity>()
                .name("xxxStatusReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(100)
                .queryString("SELECT x FROM XXXEntity x WHERE x.status = :status ORDER BY x.id")
                .parameterValues(java.util.Map.of("status", status))
                .build();
    }
} 