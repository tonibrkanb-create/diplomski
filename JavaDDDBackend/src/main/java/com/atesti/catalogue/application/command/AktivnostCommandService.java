package com.atesti.catalogue.application.command;

import com.atesti.catalogue.application.dto.command.SaveAktivnostCommand;
import com.atesti.catalogue.application.dto.query.AktivnostResponse;
import com.atesti.catalogue.domain.model.Aktivnost;
import com.atesti.catalogue.domain.repository.AktivnostRepository;
import com.atesti.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AktivnostCommandService {

    private final AktivnostRepository aktivnostRepository;

    @Transactional
    public AktivnostResponse create(SaveAktivnostCommand command) {
        Aktivnost aktivnost = Aktivnost.create(
                command.getAktivnost(),
                command.getRokTrajanja(),
                command.getCijena()
        );
        return AktivnostResponse.from(aktivnostRepository.save(aktivnost));
    }

    @Transactional
    public AktivnostResponse update(Long id, SaveAktivnostCommand command) {
        Aktivnost aktivnost = findActiveById(id);
        aktivnost.updateDetails(command.getAktivnost(), command.getRokTrajanja(), command.getCijena());
        return AktivnostResponse.from(aktivnostRepository.save(aktivnost));
    }

    @Transactional
    public AktivnostResponse deactivate(Long id) {
        Aktivnost aktivnost = findActiveById(id);
        aktivnost.deactivate();
        return AktivnostResponse.from(aktivnostRepository.save(aktivnost));
    }

    private Aktivnost findActiveById(Long id) {
        Aktivnost aktivnost = aktivnostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aktivnost not found"));
        if (!aktivnost.getIsActive()) {
            throw new ResourceNotFoundException("Aktivnost not found");
        }
        return aktivnost;
    }
}
