package opensource.alzheimerdinger.core.domain.analysis.domain.repository;

import opensource.alzheimerdinger.core.domain.analysis.domain.entity.DementiaAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DementiaAnalysisRepository extends JpaRepository<DementiaAnalysis, String> {

    @Query("""
        SELECT d FROM DementiaAnalysis d
        WHERE d.user.userId = :userId
        AND d.createdAt BETWEEN :start AND :end
        ORDER BY d.createdAt
    """)
    List<DementiaAnalysis> findByUserAndPeriod(@Param("userId") String userId,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);
}


