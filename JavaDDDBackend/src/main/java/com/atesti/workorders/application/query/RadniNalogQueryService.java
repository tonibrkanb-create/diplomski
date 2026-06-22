package com.atesti.workorders.application.query;

import com.atesti.shared.exception.ResourceNotFoundException;
import com.atesti.workorders.application.dto.query.RadniNalogDetailResponse;
import com.atesti.workorders.application.dto.query.RadniNalogResponse;
import com.atesti.workorders.application.dto.query.UskoroIsticeResponse;
import com.atesti.workorders.domain.model.RadniNalogProjection;
import com.atesti.workorders.domain.persistance.RadniNalogEntity;
import com.atesti.workorders.domain.model.UskoroIstice;
import com.atesti.workorders.domain.repository.RadniNalogRepository;
import com.atesti.workorders.domain.repository.UskoroIsticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RadniNalogQueryService {

    private final RadniNalogRepository radniNalogRepository;
    private final UskoroIsticeRepository uskoroIsticeRepository;

    public List<RadniNalogResponse> getAllNalozi() {
        return radniNalogRepository.findAllRadniNalog().stream()
                .map(RadniNalogResponse::from)
                .collect(Collectors.toList());
    }

    public RadniNalogDetailResponse getById(Long id) {
        RadniNalogProjection nalog = radniNalogRepository.findRadniNalog(id)
                .orElseThrow(() -> new ResourceNotFoundException("Radni nalog not found"));
        return RadniNalogDetailResponse.from(nalog);
    }

    public List<RadniNalogResponse> getByNaruciteljId(Long naruciteljId) {
        return radniNalogRepository.findByNaruciteljId(naruciteljId).stream()
                .map(RadniNalogResponse::from)
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

    public String getNextBrojNaloga() {
        long count = radniNalogRepository.count();
        long sequenceNumber = count + 1;

        return "RN00" + sequenceNumber;
    }
}
