package com.atesti.clients.infrastructure.kafka;

import com.atesti.clients.domain.model.Narucitelj;
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
public class NaruciteljEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String eventType, Narucitelj n) {
        try {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("eventType", eventType);
            event.put("id", n.getId());
            event.put("name", n.getName());
            event.put("adresa", n.getAdresa());
            event.put("mjesto", n.getMjesto());
            event.put("postanskiBroj", n.getPostanskiBroj());
            event.put("drzava", n.getDrzava());
            event.put("OIB", n.getOIB());
            event.put("ziroRacun", n.getZiroRacun());
            event.put("ostalo", n.getOstalo());
            event.put("kontaktOsoba", n.getKontaktOsoba());
            event.put("telefon", n.getTelefon());
            event.put("mobitel", n.getMobitel());
            event.put("fax", n.getFax());
            event.put("email", n.getEmail());
            event.put("location", n.getLocation());
            event.put("comment", n.getComment());

            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("narucitelj-events", String.valueOf(n.getId()), json);
        } catch (Exception e) {
            log.error("Failed to publish narucitelj event", e);
        }
    }
}
