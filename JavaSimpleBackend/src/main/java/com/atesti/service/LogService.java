package com.atesti.service;

import com.atesti.entity.SustavLog;
import com.atesti.entity.User;
import com.atesti.repository.SustavLogRepository;
import com.atesti.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

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

    public List<SustavLog> getAll(String entity, String action, String from, String to) {
        LocalDateTime fromDate = from != null ? LocalDateTime.parse(from + "T00:00:00") : null;
        LocalDateTime toDate = to != null ? LocalDateTime.parse(to + "T23:59:59") : null;

        return sustavLogRepository.findFiltered(
                entity != null && !entity.isBlank() ? entity : null,
                action != null && !action.isBlank() ? action : null,
                fromDate,
                toDate,
                PageRequest.of(0, 500));
    }
}
