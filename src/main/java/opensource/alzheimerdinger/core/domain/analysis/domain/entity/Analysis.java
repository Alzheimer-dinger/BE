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
@Table(name = "analysis_entries")
public class Analysis extends BaseEntity {

    @Id @Tsid
    private String analysisId;

    @Column(name = "transcript_id", nullable = false)
    private String transcriptId;

    @Column(nullable = false)
    private double riskScore;

    @Column(nullable = false) private double happy;
    @Column(nullable = false) private double sad;
    @Column(nullable = false) private double angry;
    @Column(nullable = false) private double surprised;
    @Column(nullable = false) private double bored;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
