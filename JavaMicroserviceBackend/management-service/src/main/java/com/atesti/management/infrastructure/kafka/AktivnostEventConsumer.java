package com.atesti.management.infrastructure.kafka;

import com.atesti.management.projection.model.LocalAktivnost;
import com.atesti.management.projection.repository.LocalAktivnostRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AktivnostEventConsumer {

    private final LocalAktivnostRepository localAktivnostRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "aktivnost-events", groupId = "management-service")
    public void handle(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});
            String eventType = (String) event.get("eventType");
            Object idObj = event.get("id");

            if (idObj == null) return;

            Long id = Long.valueOf(idObj.toString());

            if ("DELETED".equals(eventType)) {
                localAktivnostRepository.deleteById(id);
                return;
            }

            LocalAktivnost local = localAktivnostRepository.findById(id)
                    .orElse(LocalAktivnost.builder().id(id).build());

            Object aktivnostObj = event.get("aktivnost");
            if (aktivnostObj != null) local.setAktivnost(aktivnostObj.toString());

            Object cijenaObj = event.get("cijena");
            if (cijenaObj != null) local.setCijena(new BigDecimal(cijenaObj.toString()));

            Object rokObj = event.get("rokTrajanja");
            if (rokObj != null) local.setRokTrajanja(Integer.valueOf(rokObj.toString()));

            Object isActiveObj = event.get("isActive");
            if (isActiveObj != null) local.setIsActive(Boolean.valueOf(isActiveObj.toString()));

            localAktivnostRepository.save(local);
        } catch (Exception e) {
            log.error("Failed to process aktivnost event", e);
        }
    }
}
