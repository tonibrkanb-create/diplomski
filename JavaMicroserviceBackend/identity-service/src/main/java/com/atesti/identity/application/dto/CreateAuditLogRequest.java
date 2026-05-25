package com.atesti.identity.application.dto;

import lombok.Data;

@Data
public class CreateAuditLogRequest {
    private String entity;
    private String action;
    private Long entityId;
    private Long userId;
    private String details;
}
