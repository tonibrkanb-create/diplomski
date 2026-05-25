package com.atesti.identity.application.command;

import com.atesti.identity.domain.model.SustavLog;
import com.atesti.identity.domain.repository.SustavLogRepository;
import com.atesti.identity.domain.repository.UserRepository;
import com.atesti.identity.infrastructure.kafka.AuditLogEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogCommandService {

    private final SustavLogRepository sustavLogRepository;
    private final UserRepository userRepository;
    private final AuditLogEventPublisher auditLogEventPublisher;

    @Transactional
    public SustavLog log(String action, String entity, Long entityId, Long userId, String details) {
        SustavLog.SustavLogBuilder builder = SustavLog.builder()
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .details(details);

        if (userId != null) {
            userRepository.findById(userId).ifPresent(builder::user);
        }

        SustavLog saved = sustavLogRepository.save(builder.build());
        auditLogEventPublisher.publish(saved);
        return saved;
    }
}
