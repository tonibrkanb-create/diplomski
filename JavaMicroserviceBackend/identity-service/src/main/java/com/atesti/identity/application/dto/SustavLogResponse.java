package com.atesti.identity.application.dto;

import com.atesti.identity.domain.model.SustavLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SustavLogResponse {
    private Long id;
    private String action;
    private String entity;
    private Long entityId;
    private Long userId;
    private UserResponse user;
    private String details;
    private LocalDateTime createdAt;

    public static SustavLogResponse from(SustavLog entity) {
        return SustavLogResponse.builder()
                .id(entity.getId())
                .action(entity.getAction())
                .entity(entity.getEntity())
                .entityId(entity.getEntityId())
                .userId(entity.getUserId())
                .user(entity.getUser() != null ? UserResponse.from(entity.getUser()) : null)
                .details(entity.getDetails())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
