package com.atesti.portal.infrastructure.kafka;

import com.atesti.portal.domain.model.Recenzija;
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
public class RecenzijaEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String eventType, Recenzija recenzija) {
        try {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("eventType", eventType);
            event.put("id", recenzija.getId());
            event.put("korisnikId", recenzija.getKorisnikId());
            event.put("radniNalogId", recenzija.getRadniNalogId());
            event.put("ocjena", recenzija.getOcjena());
            event.put("komentar", recenzija.getKomentar());
            event.put("odgovor", recenzija.getOdgovor());

            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("recenzija-events", String.valueOf(recenzija.getId()), json);
        } catch (Exception e) {
            log.error("Failed to publish recenzija event", e);
        }
    }
}
