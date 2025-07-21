package opensource.alzheimerdinger.core.domain.batch.infra.config.step;

import lombok.RequiredArgsConstructor;
import opensource.alzheimerdinger.core.domain.batch.application.dto.TranscriptDto;
import opensource.alzheimerdinger.core.domain.transcript.domain.entity.Transcript;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

//Transcript 데이터 Processor - Entity를 DTO로 변환
@Component
@RequiredArgsConstructor
public class TranscriptProcessor {

    public ItemProcessor<Transcript, TranscriptDto> createDefaultProcessor() {
        return item -> {
            // 순수하게 데이터 변환만 담당
            if (item == null) {
                return null;
            }
            
            // 기본적인 null 체크만 수행
            if (item.getTranscriptId() == null || 
                item.getConversation() == null || 
                item.getConversation().isEmpty()) {
                return null; // 유효하지 않은 데이터는 스킵
            }
            
            // 전체 Transcript를 하나의 DTO로 변환
            return new TranscriptDto(
                item.getTranscriptId(),
                item.getSessionId(),
                item.getUserId(),
                item.getStartTime(),
                item.getEndTime(),
                item.getConversation()
            );
        };
    }
}