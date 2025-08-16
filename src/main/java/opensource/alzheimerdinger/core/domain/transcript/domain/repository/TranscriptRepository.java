package opensource.alzheimerdinger.core.domain.transcript.domain.repository;

import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TranscriptRepository extends MongoRepository<Transcript, String> {

    @Query(value = "{ 'user_id': ?0 }", sort = "{ 'start_time': -1 }")
    List<Transcript> findByUser(String userId);

    @Query(value = "{ 'user_id': ?0, 'start_time': { $gte: ?1, $lte: ?2 } }", sort = "{ 'start_time': 1 }")
    List<Transcript> findByUserAndPeriod(String userId, Instant startInclusive, Instant endInclusive);

    Optional<Transcript> findBySessionId(String sessionId);
}

 