package com.atesti.identity.infrastructure.kafka;

import com.atesti.identity.domain.model.User;
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
public class UserEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String eventType, User user) {
        try {
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("eventType", eventType);
            event.put("id", user.getId());
            event.put("username", user.getUsername());
            event.put("ime", user.getIme());
            event.put("prezime", user.getPrezime());
            event.put("email", user.getEmail());
            event.put("isActive", user.getIsActive());

            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("user-events", String.valueOf(user.getId()), json);
        } catch (Exception e) {
            log.error("Failed to publish user event", e);
        }
    }
}
