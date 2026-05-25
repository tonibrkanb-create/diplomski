package com.atesti.portal.infrastructure.kafka;

import com.atesti.portal.projection.model.LocalRadniNalog;
import com.atesti.portal.projection.repository.LocalRadniNalogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RadniNalogEventConsumer {

    private final LocalRadniNalogRepository localRadniNalogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "radni-nalog-events", groupId = "portal-service")
    public void handle(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});
            String eventType = (String) event.get("eventType");
            Object idObj = event.get("id");

            if (idObj == null) return;

            Long id = Long.valueOf(idObj.toString());

            if ("DELETED".equals(eventType)) {
                localRadniNalogRepository.deleteById(id);
                return;
            }

            LocalRadniNalog local = localRadniNalogRepository.findById(id)
                    .orElse(LocalRadniNalog.builder().id(id).build());

            local.setBrojNaloga(getStr(event, "brojNaloga"));

            localRadniNalogRepository.save(local);
        } catch (Exception e) {
            log.error("Failed to process radni-nalog event", e);
        }
    }

    private String getStr(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }
}
