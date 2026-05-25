package com.atesti.workorders.infrastructure.kafka;

import com.atesti.workorders.projection.model.LocalUser;
import com.atesti.workorders.projection.repository.LocalUserRepository;
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
public class UserEventConsumer {

    private final LocalUserRepository localUserRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-events", groupId = "workorders-service")
    public void handle(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, new TypeReference<>() {});
            String eventType = (String) event.get("eventType");
            Object idObj = event.get("id");

            if (idObj == null) return;

            Long id = Long.valueOf(idObj.toString());

            if ("DELETED".equals(eventType)) {
                localUserRepository.deleteById(id);
                return;
            }

            LocalUser local = localUserRepository.findById(id)
                    .orElse(LocalUser.builder().id(id).build());

            Object usernameObj = event.get("username");
            if (usernameObj != null) local.setUsername(usernameObj.toString());

            Object imeObj = event.get("ime");
            if (imeObj != null) local.setIme(imeObj.toString());

            Object prezimeObj = event.get("prezime");
            if (prezimeObj != null) local.setPrezime(prezimeObj.toString());

            Object isActiveObj = event.get("isActive");
            if (isActiveObj != null) local.setIsActive(Boolean.valueOf(isActiveObj.toString()));

            localUserRepository.save(local);
        } catch (Exception e) {
            log.error("Failed to process user event", e);
        }
    }
}
