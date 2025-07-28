package opensource.alzheimerdinger.core.domain.analysis.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;
import opensource.alzheimerdinger.core.global.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "analysis_report")
public class AnalysisReport extends BaseEntity {

    @Id @Tsid
    private String analysisReportId;

    private String report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
