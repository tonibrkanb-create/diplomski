package com.atesti.portal.infrastructure.kafka;

import com.atesti.portal.domain.model.Ponuda;
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
public class PonudaEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String eventType, Ponuda ponuda) {
        try {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("eventType", eventType);
            event.put("id", ponuda.getId());
            event.put("korisnikId", ponuda.getKorisnikId());
            event.put("opis", ponuda.getOpis());
            event.put("vrstaAtesta", ponuda.getVrstaAtesta());
            event.put("lokacija", ponuda.getLokacija());
            event.put("zeljeniDatum", ponuda.getZeljeniDatum() != null ? ponuda.getZeljeniDatum().toString() : null);
            event.put("status", ponuda.getStatus());
            event.put("odgovor", ponuda.getOdgovor());

            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("ponuda-events", String.valueOf(ponuda.getId()), json);
        } catch (Exception e) {
            log.error("Failed to publish ponuda event", e);
        }
    }
}
