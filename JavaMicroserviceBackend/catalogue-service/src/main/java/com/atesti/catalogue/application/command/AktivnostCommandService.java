package com.atesti.catalogue.application.command;

import com.atesti.catalogue.application.dto.AktivnostResponse;
import com.atesti.catalogue.application.dto.SaveAktivnostCommand;
import com.atesti.catalogue.domain.model.Aktivnost;
import com.atesti.catalogue.domain.repository.AktivnostRepository;
import com.atesti.catalogue.exception.ResourceNotFoundException;
import com.atesti.catalogue.infrastructure.kafka.AktivnostEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AktivnostCommandService {

    private final AktivnostRepository aktivnostRepository;
    private final AktivnostEventPublisher aktivnostEventPublisher;

    @Transactional
    public AktivnostResponse create(SaveAktivnostCommand command) {
        Aktivnost aktivnost = Aktivnost.create(
                command.getAktivnost(),
                command.getRokTrajanja(),
                command.getCijena()
        );
        aktivnost = aktivnostRepository.save(aktivnost);
        aktivnostEventPublisher.publish("CREATED", aktivnost);
        return AktivnostResponse.from(aktivnost);
    }

    @Transactional
    public AktivnostResponse update(Long id, SaveAktivnostCommand command) {
        Aktivnost aktivnost = findActiveById(id);
        aktivnost.updateDetails(command.getAktivnost(), command.getRokTrajanja(), command.getCijena());
        aktivnost = aktivnostRepository.save(aktivnost);
        aktivnostEventPublisher.publish("UPDATED", aktivnost);
        return AktivnostResponse.from(aktivnost);
    }

    @Transactional
    public AktivnostResponse deactivate(Long id) {
        Aktivnost aktivnost = findActiveById(id);
        aktivnost.deactivate();
        aktivnost = aktivnostRepository.save(aktivnost);
        aktivnostEventPublisher.publish("DELETED", aktivnost);
        return AktivnostResponse.from(aktivnost);
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
