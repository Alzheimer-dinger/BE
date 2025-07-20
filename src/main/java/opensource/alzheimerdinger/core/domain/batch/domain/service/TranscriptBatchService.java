package opensource.alzheimerdinger.core.domain.batch.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptBatchService {

    private final MongoTemplate mongoTemplate;

    //특정 유저의 특정 기간 Transcript 조회
    public List<Transcript> findByUserIdAndPeriod(String userId, LocalDateTime fromDate, LocalDateTime toDate) {
        Criteria criteria = Criteria.where("startTime").gte(fromDate).lt(toDate);
        
        if (userId != null && !userId.trim().isEmpty()) {
            criteria.and("userId").is(userId);
        }
        
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Transcript.class);
    }

    //모든 유저의 특정 기간 Transcript 조회 (스케줄러용)
    public List<Transcript> findByPeriod(LocalDateTime fromDate, LocalDateTime toDate) {
        Query query = new Query(Criteria.where("startTime").gte(fromDate).lt(toDate));
        return mongoTemplate.find(query, Transcript.class);
    }
} 