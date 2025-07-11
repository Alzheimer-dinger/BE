package opensource.alzheimerdinger.core.domain.transcript.domain.repository;

import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranscriptRepository extends MongoRepository<Transcript, String> {
}
