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
@Table(name = "emotion_analysis")
public class EmotionAnalysis extends BaseEntity {

    @Id @Tsid
    @Column(name = "id")
    private String emotionId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false) private double happy;
    @Column(nullable = false) private double sad;
    @Column(nullable = false) private double angry;
    @Column(nullable = false) private double surprised;
    @Column(nullable = false) private double bored;
}


