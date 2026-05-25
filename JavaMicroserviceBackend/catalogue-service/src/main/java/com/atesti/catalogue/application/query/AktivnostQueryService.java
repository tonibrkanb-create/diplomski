package com.atesti.catalogue.application.query;

import com.atesti.catalogue.application.dto.AktivnostResponse;
import com.atesti.catalogue.domain.model.Aktivnost;
import com.atesti.catalogue.domain.repository.AktivnostRepository;
import com.atesti.catalogue.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AktivnostQueryService {

    private final AktivnostRepository aktivnostRepository;

    public List<AktivnostResponse> getAllActive() {
        return aktivnostRepository.findByIsActiveTrueOrderByIdAsc().stream()
                .map(AktivnostResponse::from)
                .toList();
    }

    public AktivnostResponse getById(Long id) {
        Aktivnost aktivnost = aktivnostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aktivnost not found"));
        if (!aktivnost.getIsActive()) {
            throw new ResourceNotFoundException("Aktivnost not found");
        }
        return AktivnostResponse.from(aktivnost);
    }
}
