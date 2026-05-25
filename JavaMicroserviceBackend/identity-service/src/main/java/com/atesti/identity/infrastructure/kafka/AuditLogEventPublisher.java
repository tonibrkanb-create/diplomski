package com.atesti.identity.infrastructure.kafka;

import com.atesti.identity.domain.model.SustavLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(SustavLog sustavLog) {
        try {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("eventType", "CREATED");
            event.put("id", sustavLog.getId());
            event.put("action", sustavLog.getAction());
            event.put("entity", sustavLog.getEntity());
            event.put("entityId", sustavLog.getEntityId());
            event.put("userId", sustavLog.getUserId());
            event.put("user", sustavLog.getUser() != null ? sustavLog.getUser().displayName() : null);
            event.put("details", sustavLog.getDetails());
            event.put("createdAt", sustavLog.getCreatedAt() != null ? sustavLog.getCreatedAt().toString() : null);

            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("audit-log-events", String.valueOf(sustavLog.getId()), json);
        } catch (Exception e) {
            log.error("Failed to publish audit log event", e);
        }
    }
}
