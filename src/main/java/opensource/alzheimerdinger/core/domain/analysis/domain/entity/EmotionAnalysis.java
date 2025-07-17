package opensource.alzheimerdinger.core.domain.analysis.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emotion_analysis",
        indexes = {@Index(name = "idx_emotion_session", columnList = "session_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionAnalysis {
    @Id
    @Column(name = "analysis_id", length = 36)
    private String analysisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_emotion_session"))
    private ConversationSession session;

    @Column(name = "analysis_time", nullable = false)
    private LocalDateTime analysisTime;

    @Column(name = "emotion_label", length = 50, nullable = false)
    private String emotionLabel;

    @Column(name = "emotion_score", nullable = false)
    private Float emotionScore;
}