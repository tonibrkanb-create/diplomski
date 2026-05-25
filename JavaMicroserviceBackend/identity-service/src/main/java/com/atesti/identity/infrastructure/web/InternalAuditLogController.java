package com.atesti.identity.infrastructure.web;

import com.atesti.identity.application.command.AuditLogCommandService;
import com.atesti.identity.application.dto.CreateAuditLogRequest;
import com.atesti.identity.application.dto.SustavLogResponse;
import com.atesti.identity.application.query.AuditLogQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class InternalAuditLogController {

    private final AuditLogCommandService auditLogCommandService;
    private final AuditLogQueryService auditLogQueryService;

    @Value("${internal.secret}")
    private String internalSecret;

    @PostMapping("/api/internal/audit-logs")
    public ResponseEntity<?> createAuditLog(
            @RequestHeader("X-Internal-Secret") String secret,
            @RequestBody CreateAuditLogRequest request) {
        if (!internalSecret.equals(secret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Invalid internal secret"));
        }

        auditLogCommandService.log(
                request.getAction(),
                request.getEntity(),
                request.getEntityId(),
                request.getUserId(),
                request.getDetails()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("status", "ok"));
    }

    @GetMapping("/api/management/logs")
    public ResponseEntity<List<SustavLogResponse>> getLogs(
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(auditLogQueryService.getFiltered(entity, action, from, to));
    }
}
