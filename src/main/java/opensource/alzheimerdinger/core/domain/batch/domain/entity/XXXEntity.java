package opensource.alzheimerdinger.core.domain.batch.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import opensource.alzheimerdinger.core.global.common.BaseEntity;
import java.time.LocalDateTime;

@Entity
@Table(name = "xxx_table")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XXXEntity extends BaseEntity {

    @Id @Tsid
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String status;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "processed_by")
    private String processedBy;
} 