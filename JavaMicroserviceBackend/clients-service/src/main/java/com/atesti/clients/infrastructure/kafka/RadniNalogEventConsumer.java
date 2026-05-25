package com.atesti.clients.infrastructure.kafka;

import com.atesti.clients.projection.model.LocalRadniNalogCount;
import com.atesti.clients.projection.repository.LocalRadniNalogCountRepository;
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

    private final LocalRadniNalogCountRepository localRadniNalogCountRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "radni-nalog-events", groupId = "clients-service")
    public void handle(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});
            String eventType = (String) event.get("eventType");
            Object naruciteljIdObj = event.get("naruciteljId");

            if (naruciteljIdObj == null) return;

            Long naruciteljId = Long.valueOf(naruciteljIdObj.toString());

            LocalRadniNalogCount counter = localRadniNalogCountRepository.findById(naruciteljId)
                    .orElse(LocalRadniNalogCount.builder().naruciteljId(naruciteljId).count(0L).build());

            if ("CREATED".equals(eventType)) {
                counter.setCount(counter.getCount() + 1);
            } else if ("DELETED".equals(eventType) && counter.getCount() > 0) {
                counter.setCount(counter.getCount() - 1);
            }

            localRadniNalogCountRepository.save(counter);
        } catch (Exception e) {
            log.error("Failed to process radni-nalog event", e);
        }
    }
}
