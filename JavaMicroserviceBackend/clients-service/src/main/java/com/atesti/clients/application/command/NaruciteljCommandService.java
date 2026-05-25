package com.atesti.clients.application.command;

import com.atesti.clients.application.dto.NaruciteljResponse;
import com.atesti.clients.application.dto.SaveNaruciteljCommand;
import com.atesti.clients.domain.model.Narucitelj;
import com.atesti.clients.domain.repository.NaruciteljRepository;
import com.atesti.clients.exception.BadRequestException;
import com.atesti.clients.exception.ResourceNotFoundException;
import com.atesti.clients.infrastructure.kafka.NaruciteljEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NaruciteljCommandService {

    private final NaruciteljRepository naruciteljRepository;
    private final NaruciteljEventPublisher naruciteljEventPublisher;

    @Transactional
    public NaruciteljResponse create(SaveNaruciteljCommand command) {
        String nameValue = command.resolveName();
        if (nameValue == null || nameValue.isBlank()) {
            throw new BadRequestException("Error creating narucitelj: name is required");
        }

        Narucitelj narucitelj = Narucitelj.builder()
                .name(nameValue)
                .adresa(command.getAdresa())
                .mjesto(command.getMjesto())
                .postanskiBroj(command.getPostanskiBroj())
                .drzava(command.getDrzava())
                .OIB(command.getOIB())
                .ziroRacun(command.getZiroRacun())
                .ostalo(command.getOstalo())
                .kontaktOsoba(command.getKontaktOsoba())
                .telefon(command.getTelefon())
                .mobitel(command.getMobitel())
                .fax(command.getFax())
                .email(command.getEmail())
                .location(command.getMjesto() != null ? command.getMjesto() : "")
                .comment(command.getComment())
                .build();

        narucitelj = naruciteljRepository.save(narucitelj);
        naruciteljEventPublisher.publish("CREATED", narucitelj);
        return NaruciteljResponse.from(narucitelj);
    }

    @Transactional
    public NaruciteljResponse update(Long id, SaveNaruciteljCommand command) {
        Narucitelj narucitelj = naruciteljRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Narucitelj not found"));

        String nameValue = command.resolveName();
        narucitelj.updateFrom(nameValue, command.getAdresa(), command.getMjesto(),
                command.getPostanskiBroj(), command.getDrzava(), command.getOIB(),
                command.getZiroRacun(), command.getOstalo(), command.getKontaktOsoba(),
                command.getTelefon(), command.getMobitel(), command.getFax(),
                command.getEmail(), command.getLocation(), command.getComment());

        narucitelj = naruciteljRepository.save(narucitelj);
        naruciteljEventPublisher.publish("UPDATED", narucitelj);
        return NaruciteljResponse.from(narucitelj);
    }

    @Transactional
    public NaruciteljResponse delete(Long id) {
        Narucitelj narucitelj = naruciteljRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Narucitelj not found"));
        naruciteljRepository.delete(narucitelj);
        naruciteljEventPublisher.publish("DELETED", narucitelj);
        return NaruciteljResponse.from(narucitelj);
    }
}
