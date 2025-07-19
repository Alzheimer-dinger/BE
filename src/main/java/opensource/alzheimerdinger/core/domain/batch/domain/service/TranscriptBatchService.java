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

    private final MongoTemplate batchMongoTemplate;

    //특정 날짜 이후의 Transcript 조회 (startTime 기준)
    public List<Transcript> findByStartTimeAfter(LocalDateTime date) {
        Query query = new Query(Criteria.where("startTime").gte(date));
        return batchMongoTemplate.find(query, Transcript.class);
    }

    //특정 ID들의 Transcript 조회
    public List<Transcript> findByIds(List<String> ids) {
        Query query = new Query(Criteria.where("transcriptId").in(ids));
        return batchMongoTemplate.find(query, Transcript.class);
    }
} 