package opensource.alzheimerdinger.core.domain.analysis.domain.repository;

import opensource.alzheimerdinger.core.domain.analysis.domain.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AnalysisRepository extends JpaRepository<Analysis, String> {

    @Query("""
        SELECT a FROM Analysis a 
        WHERE a.user.userId = :userId 
        AND a.createdAt BETWEEN :start AND :end 
        ORDER BY a.createdAt
    """)
    List<Analysis> findByUserAndPeriod(@Param("userId") String userId, 
                                       @Param("start") LocalDateTime start, 
                                       @Param("end") LocalDateTime end);
}
