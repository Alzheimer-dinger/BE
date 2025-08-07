package opensource.alzheimerdinger.core.domain.feedback.domain.repository;

import opensource.alzheimerdinger.core.domain.feedback.domain.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, String> {
}
