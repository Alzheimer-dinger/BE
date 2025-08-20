package opensource.alzheimerdinger.core.domain.analysis.domain.repository;

import opensource.alzheimerdinger.core.domain.analysis.domain.entity.AnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, String> {

    @Query(value = """
        SELECT r.* 
        FROM reports r 
        JOIN users u ON r.user_id = u.user_id
        WHERE u.user_id = :userId AND r.created_at <= :periodEnd 
        ORDER BY r.created_at DESC 
        LIMIT 1
        """, nativeQuery = true)
    Optional<AnalysisReport> findLatestReport(@Param("userId") String userId,
                                              @Param("periodEnd") LocalDateTime periodEnd);
}
