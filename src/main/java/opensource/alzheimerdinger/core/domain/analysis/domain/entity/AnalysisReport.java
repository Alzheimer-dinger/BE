package opensource.alzheimerdinger.core.domain.analysis.domain.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import opensource.alzheimerdinger.core.global.common.BaseEntity;
import opensource.alzheimerdinger.core.domain.user.domain.entity.User;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reports")
public class AnalysisReport extends BaseEntity {

    @Id @Tsid
    @Column(name = "id")
    private String analysisReportId;

    // Self reference to base report (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_report_id")
    private AnalysisReport baseReport;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
