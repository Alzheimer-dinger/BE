package opensource.alzheimerdinger.core.domain.analysis.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cognitive_reports",
        indexes = {@Index(name = "idx_report_session", columnList = "session_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CognitiveReport {
    @Id
    @Column(name = "report_id", length = 36)
    private String reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_report_session"))
    private ConversationSession session;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @Column(name = "risk_level", length = 10, nullable = false)
    private String riskLevel;  // LOW, MEDIUM, HIGH

    @Column(name = "interpretation", length = 255, nullable = false)
    private String interpretation;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}