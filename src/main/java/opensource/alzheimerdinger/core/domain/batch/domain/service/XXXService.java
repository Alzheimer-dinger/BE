package opensource.alzheimerdinger.core.domain.batch.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opensource.alzheimerdinger.core.domain.batch.domain.entity.XXXEntity;
import opensource.alzheimerdinger.core.domain.batch.domain.repository.XXXRepository;
import opensource.alzheimerdinger.core.domain.batch.application.dto.XXXDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class XXXService {

    private final XXXRepository xxxRepository;

    public XXXDto processXXX(XXXEntity entity) {
        try {
            return convertToDto(entity);
        } catch (Exception e) {
            log.error("Error processing XXX entity: {}", entity.getId(), e);
            throw e;
        }
    }

    @Transactional
    public void batchSave(List<? extends XXXDto> dtos) {
        try {
            List<XXXEntity> entities = dtos.stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toList());
            
            xxxRepository.saveAll(entities);
            log.info("Batch saved {} entities", entities.size());
        } catch (Exception e) {
            log.error("Error batch saving entities", e);
            throw e;
        }
    }

    public XXXDto findById(String id) {
        return xxxRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("XXX not found with id: " + id));
    }

    public List<XXXDto> findByIds(List<String> ids) {
        return xxxRepository.findByIdIn(ids)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateProcessStatus(String id, String processedBy) {
        XXXEntity entity = xxxRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("XXX not found with id: " + id));
        
        XXXEntity updatedEntity = XXXEntity.builder()
                .id(entity.getId())
                .name(entity.getName())
                .status("PROCESSED")
                .processedAt(LocalDateTime.now())
                .processedBy(processedBy)
                .build();
        
        xxxRepository.save(updatedEntity);
    }

    public boolean isValidDto(XXXDto dto) {
        return dto.id() != null && !dto.id().trim().isEmpty() && 
               dto.name() != null && !dto.name().trim().isEmpty();
    }

    public boolean isProcessed(XXXDto dto) {
        return "PROCESSED".equals(dto.status()) && dto.processedAt() != null;
    }

    public boolean isProcessable(XXXEntity entity) {
        return "PENDING".equals(entity.getStatus());
    }

    private XXXDto convertToDto(XXXEntity entity) {
        return new XXXDto(
                entity.getId(),
                entity.getName(),
                entity.getStatus(),
                entity.getProcessedAt(),
                entity.getProcessedBy()
        );
    }

    private XXXEntity convertToEntity(XXXDto dto) {
        return XXXEntity.builder()
                .id(dto.id())
                .name(dto.name())
                .status(dto.status())
                .processedAt(dto.processedAt())
                .processedBy(dto.processedBy())
                .build();
    }
} 