package opensource.alzheimerdinger.core.domain.analysis.domain.repository;

import opensource.alzheimerdinger.core.domain.analysis.domain.entity.AnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, String> {

    @Query(value = """
        SELECT ar.* FROM analysis_report ar 
        JOIN users u ON ar.user_id = u.user_id 
        WHERE u.user_id = :userId AND ar.created_at <= :periodEnd 
        ORDER BY ar.created_at DESC 
        LIMIT 1
        """, nativeQuery = true)
    Optional<AnalysisReport> findLatestReport(@Param("userId") String userId, 
                                             @Param("periodEnd") LocalDateTime periodEnd);
}
