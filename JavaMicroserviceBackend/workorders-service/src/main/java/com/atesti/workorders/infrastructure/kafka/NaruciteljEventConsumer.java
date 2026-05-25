package com.atesti.workorders.infrastructure.kafka;

import com.atesti.workorders.projection.model.LocalNarucitelj;
import com.atesti.workorders.projection.repository.LocalNaruciteljRepository;
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
public class NaruciteljEventConsumer {

    private final LocalNaruciteljRepository localNaruciteljRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "narucitelj-events", groupId = "workorders-service")
    public void handle(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});
            String eventType = (String) event.get("eventType");
            Object idObj = event.get("id");

            if (idObj == null) return;

            Long id = Long.valueOf(idObj.toString());

            if ("DELETED".equals(eventType)) {
                localNaruciteljRepository.deleteById(id);
                return;
            }

            LocalNarucitelj local = localNaruciteljRepository.findById(id)
                    .orElse(LocalNarucitelj.builder().id(id).build());

            local.setName(getStr(event, "name"));
            local.setAdresa(getStr(event, "adresa"));
            local.setMjesto(getStr(event, "mjesto"));
            local.setPostanskiBroj(getStr(event, "postanskiBroj"));
            local.setDrzava(getStr(event, "drzava"));
            local.setOIB(getStr(event, "OIB"));
            local.setZiroRacun(getStr(event, "ziroRacun"));
            local.setOstalo(getStr(event, "ostalo"));
            local.setKontaktOsoba(getStr(event, "kontaktOsoba"));
            local.setTelefon(getStr(event, "telefon"));
            local.setMobitel(getStr(event, "mobitel"));
            local.setFax(getStr(event, "fax"));
            local.setEmail(getStr(event, "email"));
            local.setLocation(getStr(event, "location"));
            local.setComment(getStr(event, "comment"));

            localNaruciteljRepository.save(local);
        } catch (Exception e) {
            log.error("Failed to process narucitelj event", e);
        }
    }

    private String getStr(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }
}
