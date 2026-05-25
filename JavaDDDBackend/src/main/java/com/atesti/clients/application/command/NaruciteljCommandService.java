package com.atesti.clients.application.command;

import com.atesti.clients.application.dto.command.SaveNaruciteljCommand;
import com.atesti.clients.application.dto.query.NaruciteljResponse;
import com.atesti.clients.domain.model.Narucitelj;
import com.atesti.clients.domain.repository.NaruciteljRepository;
import com.atesti.shared.exception.BadRequestException;
import com.atesti.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NaruciteljCommandService {

    private final NaruciteljRepository naruciteljRepository;

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

        return NaruciteljResponse.from(naruciteljRepository.save(narucitelj));
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

        return NaruciteljResponse.from(naruciteljRepository.save(narucitelj));
    }

    @Transactional
    public NaruciteljResponse delete(Long id) {
        Narucitelj narucitelj = naruciteljRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Narucitelj not found"));
        naruciteljRepository.delete(narucitelj);
        return NaruciteljResponse.from(narucitelj);
    }
}
