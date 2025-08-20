package opensource.alzheimerdinger.core.domain.analysis.domain.repository;

import opensource.alzheimerdinger.core.domain.analysis.domain.entity.EmotionAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EmotionAnalysisRepository extends JpaRepository<EmotionAnalysis, String> {

    @Query("""
        SELECT e FROM EmotionAnalysis e
        WHERE e.user.userId = :userId
        AND e.createdAt BETWEEN :start AND :end
        ORDER BY e.createdAt
    """)
    List<EmotionAnalysis> findByUserAndPeriod(@Param("userId") String userId,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);
}


