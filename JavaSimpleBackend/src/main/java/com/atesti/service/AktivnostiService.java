package com.atesti.service;

import com.atesti.dto.AktivnostRequest;
import com.atesti.entity.Aktivnost;
import com.atesti.exception.BadRequestException;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.AktivnostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AktivnostiService {

    private final AktivnostRepository aktivnostRepository;

    public List<Aktivnost> getAllAktivnosti() {
        return aktivnostRepository.findByIsActiveTrueOrderByIdAsc();
    }

    public Aktivnost getAktivnostById(Long id) {
        Aktivnost aktivnost = aktivnostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aktivnost not found"));

        if (!aktivnost.getIsActive()) {
            throw new ResourceNotFoundException("Aktivnost not found");
        }

        return aktivnost;
    }

    public Aktivnost createAktivnost(AktivnostRequest request) {
        if (request.getAktivnost() == null || request.getRokTrajanja() == null) {
            throw new BadRequestException("Error creating aktivnost: aktivnost and rokTrajanja are required");
        }

        Aktivnost aktivnost = Aktivnost.builder()
                .aktivnost(request.getAktivnost())
                .rokTrajanja(request.getRokTrajanja())
                .cijena(request.getCijena())
                .isActive(true)
                .build();

        return aktivnostRepository.save(aktivnost);
    }

    public Aktivnost updateAktivnost(Long id, AktivnostRequest request) {
        Aktivnost aktivnost = aktivnostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aktivnost not found"));

        if (!aktivnost.getIsActive()) {
            throw new ResourceNotFoundException("Aktivnost not found");
        }

        if (request.getAktivnost() != null) {
            aktivnost.setAktivnost(request.getAktivnost());
        }
        if (request.getRokTrajanja() != null) {
            aktivnost.setRokTrajanja(request.getRokTrajanja());
        }
        if (request.getCijena() != null) {
            aktivnost.setCijena(request.getCijena());
        }

        return aktivnostRepository.save(aktivnost);
    }

    public Aktivnost deleteAktivnost(Long id) {
        Aktivnost aktivnost = aktivnostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aktivnost not found"));

        if (!aktivnost.getIsActive()) {
            throw new ResourceNotFoundException("Aktivnost not found");
        }

        aktivnost.setIsActive(false);
        return aktivnostRepository.save(aktivnost);
    }
}
