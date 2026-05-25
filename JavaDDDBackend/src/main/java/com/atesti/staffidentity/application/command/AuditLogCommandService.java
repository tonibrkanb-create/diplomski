package com.atesti.staffidentity.application.command;

import com.atesti.staffidentity.domain.model.SustavLog;
import com.atesti.staffidentity.domain.repository.SustavLogRepository;
import com.atesti.staffidentity.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogCommandService {

    private final SustavLogRepository sustavLogRepository;
    private final UserRepository userRepository;

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

        return sustavLogRepository.save(builder.build());
    }
}
