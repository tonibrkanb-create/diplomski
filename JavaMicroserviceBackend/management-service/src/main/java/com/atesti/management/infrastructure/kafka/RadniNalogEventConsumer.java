package com.atesti.management.infrastructure.kafka;

import com.atesti.management.projection.model.LocalRadniNalog;
import com.atesti.management.projection.repository.LocalRadniNalogRepository;
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
public class RadniNalogEventConsumer {

    private final LocalRadniNalogRepository localRadniNalogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "radni-nalog-events", groupId = "management-service")
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

            Object brojNalogaObj = event.get("brojNaloga");
            if (brojNalogaObj != null) local.setBrojNaloga(brojNalogaObj.toString());

            Object naruciteljIdObj = event.get("naruciteljId");
            if (naruciteljIdObj != null) local.setNaruciteljId(Long.valueOf(naruciteljIdObj.toString()));

            Object assignedUserIdObj = event.get("assignedUserId");
            if (assignedUserIdObj != null) local.setAssignedUserId(Long.valueOf(assignedUserIdObj.toString()));

            Object aktivnostiObj = event.get("aktivnosti");
            if (aktivnostiObj != null) local.setAktivnosti(aktivnostiObj.toString());

            Object zavrsenoObj = event.get("zavrseno");
            if (zavrsenoObj != null) local.setZavrseno(Boolean.valueOf(zavrsenoObj.toString()));

            Object faktObj = event.get("fakturirano");
            if (faktObj != null) local.setFakturirano(Boolean.valueOf(faktObj.toString()));

            Object datumObj = event.get("datum");
            if (datumObj != null) {
                local.setDatum(LocalDateTime.parse(datumObj.toString()));
            }

            localRadniNalogRepository.save(local);
        } catch (Exception e) {
            log.error("Failed to process radni-nalog event", e);
        }
    }
}
