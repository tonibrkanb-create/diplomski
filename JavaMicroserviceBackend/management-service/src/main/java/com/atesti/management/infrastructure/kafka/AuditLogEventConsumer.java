package com.atesti.management.infrastructure.kafka;

import com.atesti.management.projection.model.LocalSustavLog;
import com.atesti.management.projection.repository.LocalSustavLogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogEventConsumer {

    private final LocalSustavLogRepository localSustavLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "audit-log-events", groupId = "management-service")
    public void handle(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});
            Object idObj = event.get("id");

            if (idObj == null) return;

            Long id = Long.valueOf(idObj.toString());

            LocalSustavLog local = localSustavLogRepository.findById(id)
                    .orElse(LocalSustavLog.builder().id(id).build());

            Object actionObj = event.get("action");
            if (actionObj != null) local.setAction(actionObj.toString());

            Object entityObj = event.get("entity");
            if (entityObj != null) local.setEntity(entityObj.toString());

            Object entityIdObj = event.get("entityId");
            if (entityIdObj != null) local.setEntityId(Long.valueOf(entityIdObj.toString()));

            Object userIdObj = event.get("userId");
            if (userIdObj != null) local.setUserId(Long.valueOf(userIdObj.toString()));

            Object userNameObj = event.get("user");
            if (userNameObj != null) local.setUserName(userNameObj.toString());

            Object detailsObj = event.get("details");
            if (detailsObj != null) local.setDetails(detailsObj.toString());

            Object createdAtObj = event.get("createdAt");
            if (createdAtObj != null) {
                local.setCreatedAt(LocalDateTime.parse(createdAtObj.toString()));
            }

            localSustavLogRepository.save(local);
        } catch (Exception e) {
            log.error("Failed to process audit-log event", e);
        }
    }
}
