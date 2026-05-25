package com.atesti.workorders.infrastructure.kafka;

import com.atesti.workorders.domain.model.RadniNalog;
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
public class RadniNalogEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String eventType, RadniNalog nalog) {
        try {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("eventType", eventType);
            event.put("id", nalog.getId());
            event.put("brojNaloga", nalog.getBrojNaloga());
            event.put("datum", nalog.getDatum() != null ? nalog.getDatum().toString() : null);
            event.put("objekt", nalog.getObjekt());
            event.put("fakturirano", nalog.getFakturirano());
            event.put("zavrseno", nalog.getZavrseno());
            event.put("opis", nalog.getOpis());
            event.put("brojPonude", nalog.getBrojPonude());
            event.put("brojRacuna", nalog.getBrojRacuna());
            event.put("narudzbenica", nalog.getNarudzbenica());
            event.put("ugovor", nalog.getUgovor());
            event.put("aktivnosti", nalog.getAktivnosti());
            event.put("pdfUrl", nalog.getPdfUrl());
            event.put("naruciteljId", nalog.getNaruciteljId());
            event.put("assignedUserId", nalog.getAssignedUserId());

            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("radni-nalog-events", String.valueOf(nalog.getId()), json);
        } catch (Exception e) {
            log.error("Failed to publish radni-nalog event", e);
        }
    }
}
