package opensource.alzheimerdinger.core.domain.analysis.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emotion_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionAnalysis {
    @Id
    private String analysisId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    private LocalDateTime timestamp;

    @Column(nullable = false) private float happy;
    @Column(nullable = false) private float sad;
    @Column(nullable = false) private float angry;
    @Column(nullable = false) private float surprised;
    @Column(nullable = false) private float bored;
}