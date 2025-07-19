package opensource.alzheimerdinger.core.domain.analysis.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cognitive_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CognitiveReport {
    @Id
    private String reportId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private float riskScore;

    public enum RiskLabel {NORMAL, RISK}
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLabel riskLabel;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}