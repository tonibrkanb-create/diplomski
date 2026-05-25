package com.atesti.workorders.application.query;

import com.atesti.workorders.application.dto.query.RadniNalogResponse;
import com.atesti.workorders.application.dto.query.UskoroIsticeResponse;
import com.atesti.workorders.domain.model.RadniNalog;
import com.atesti.workorders.domain.model.UskoroIstice;
import com.atesti.workorders.domain.repository.RadniNalogRepository;
import com.atesti.workorders.domain.repository.UskoroIsticeRepository;
import com.atesti.workorders.exception.ResourceNotFoundException;
import com.atesti.workorders.projection.model.LocalNarucitelj;
import com.atesti.workorders.projection.model.LocalUser;
import com.atesti.workorders.projection.repository.LocalNaruciteljRepository;
import com.atesti.workorders.projection.repository.LocalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RadniNalogQueryService {

    private final RadniNalogRepository radniNalogRepository;
    private final UskoroIsticeRepository uskoroIsticeRepository;
    private final LocalNaruciteljRepository localNaruciteljRepository;
    private final LocalUserRepository localUserRepository;

    public List<RadniNalogResponse> getAll() {
        return radniNalogRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public RadniNalogResponse getById(Long id) {
        RadniNalog nalog = radniNalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Radni nalog not found"));
        return toResponse(nalog);
    }

    public List<RadniNalogResponse> getByNaruciteljId(Long naruciteljId) {
        return radniNalogRepository.findByNaruciteljId(naruciteljId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<UskoroIsticeResponse> getUskoroIstice(int days) {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(days);

        List<UskoroIstice> items = uskoroIsticeRepository
                .findByIsActiveTrueAndDatumIstekaBetweenOrderByDatumIstekaAsc(today, threshold);

        return items.stream()
                .map(UskoroIsticeResponse::from)
                .collect(Collectors.toList());
    }

    private RadniNalogResponse toResponse(RadniNalog nalog) {
        LocalNarucitelj narucitelj = nalog.getNaruciteljId() != null
                ? localNaruciteljRepository.findById(nalog.getNaruciteljId()).orElse(null) : null;
        LocalUser user = nalog.getAssignedUserId() != null
                ? localUserRepository.findById(nalog.getAssignedUserId()).orElse(null) : null;
        return RadniNalogResponse.from(nalog, narucitelj, user);
    }
}
