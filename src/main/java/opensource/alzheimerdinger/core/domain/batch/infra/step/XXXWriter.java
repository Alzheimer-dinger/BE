package opensource.alzheimerdinger.core.domain.batch.infra.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.application.dto.XXXDto;
import opensource.alzheimerdinger.core.domain.batch.domain.service.XXXService;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class XXXWriter {

    private final XXXService xxxService;

    public ItemWriter<XXXDto> createDefaultWriter() {
        return items -> {
            try {
                log.info("Writing {} items", items.size());
                xxxService.batchSave(items.getItems());
            } catch (Exception e) {
                log.error("Error writing items", e);
                throw e;
            }
        };
    }

    public ItemWriter<XXXDto> createStatusUpdateWriter(String processedBy) {
        return items -> {
            try {
                log.info("Updating status for {} items", items.size());
                
                for (XXXDto item : items.getItems()) {
                    xxxService.updateProcessStatus(item.id(), processedBy);
                }
                
            } catch (Exception e) {
                log.error("Error updating status", e);
                throw e;
            }
        };
    }
} 