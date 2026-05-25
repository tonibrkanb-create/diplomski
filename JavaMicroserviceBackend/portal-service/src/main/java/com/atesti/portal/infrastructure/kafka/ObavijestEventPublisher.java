package com.atesti.portal.infrastructure.kafka;

import com.atesti.portal.domain.model.Obavijest;
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
public class ObavijestEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String eventType, Obavijest obavijest) {
        try {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("eventType", eventType);
            event.put("id", obavijest.getId());
            event.put("korisnikId", obavijest.getKorisnikId());
            event.put("naslov", obavijest.getNaslov());
            event.put("poruka", obavijest.getPoruka());
            event.put("procitana", obavijest.getProcitana());

            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("obavijest-events", String.valueOf(obavijest.getId()), json);
        } catch (Exception e) {
            log.error("Failed to publish obavijest event", e);
        }
    }
}
