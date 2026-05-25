package com.atesti.catalogue.infrastructure.kafka;

import com.atesti.catalogue.domain.model.Aktivnost;
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
public class AktivnostEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String eventType, Aktivnost aktivnost) {
        try {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("eventType", eventType);
            event.put("id", aktivnost.getId());
            event.put("aktivnost", aktivnost.getAktivnost());
            event.put("rokTrajanja", aktivnost.getRokTrajanja());
            event.put("cijena", aktivnost.getCijena());
            event.put("isActive", aktivnost.getIsActive());

            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("aktivnost-events", String.valueOf(aktivnost.getId()), json);
        } catch (Exception e) {
            log.error("Failed to publish aktivnost event", e);
        }
    }
}
