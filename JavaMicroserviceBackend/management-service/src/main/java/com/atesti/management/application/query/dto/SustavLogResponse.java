package com.atesti.management.application.query.dto;

import com.atesti.management.projection.model.LocalSustavLog;
import lombok.*;

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
    private String userName;
    private String details;
    private LocalDateTime createdAt;

    public static SustavLogResponse from(LocalSustavLog log) {
        return SustavLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .entity(log.getEntity())
                .entityId(log.getEntityId())
                .userId(log.getUserId())
                .userName(log.getUserName())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
