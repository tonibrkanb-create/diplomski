package com.atesti.staffidentity.application.query;

import com.atesti.staffidentity.application.dto.query.SustavLogResponse;
import com.atesti.staffidentity.domain.repository.SustavLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogQueryService {

    private final SustavLogRepository sustavLogRepository;

    public List<SustavLogResponse> getFiltered(String entity, String action, String from, String to) {
        LocalDateTime fromDate = from != null ? LocalDateTime.parse(from + "T00:00:00") : null;
        LocalDateTime toDate = to != null ? LocalDateTime.parse(to + "T23:59:59") : null;

        return sustavLogRepository.findFiltered(
                entity != null && !entity.isBlank() ? entity : null,
                action != null && !action.isBlank() ? action : null,
                fromDate,
                toDate,
                PageRequest.of(0, 500)
        ).stream().map(SustavLogResponse::from).toList();
    }
}
