package com.atesti.portal.application.command;

import com.atesti.portal.application.dto.SendObavijestCommand;
import com.atesti.portal.application.dto.ObavijestResponse;
import com.atesti.portal.domain.model.Korisnik;
import com.atesti.portal.domain.model.Obavijest;
import com.atesti.portal.domain.repository.KorisnikRepository;
import com.atesti.portal.domain.repository.ObavijestRepository;
import com.atesti.portal.exception.BadRequestException;
import com.atesti.portal.exception.ResourceNotFoundException;
import com.atesti.portal.infrastructure.kafka.ObavijestEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObavijestCommandService {

    private final ObavijestRepository obavijestRepository;
    private final KorisnikRepository korisnikRepository;
    private final ObavijestEventPublisher obavijestEventPublisher;

    @Transactional
    public ObavijestResponse create(SendObavijestCommand command) {
        if (command.getKorisnikId() == null || command.getNaslov() == null || command.getPoruka() == null) {
            throw new BadRequestException("Sva polja su obavezna");
        }

        Korisnik korisnik = korisnikRepository.findById(command.getKorisnikId())
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));

        Obavijest obavijest = Obavijest.builder()
                .korisnik(korisnik)
                .naslov(command.getNaslov())
                .poruka(command.getPoruka())
                .procitana(false)
                .build();

        Obavijest saved = obavijestRepository.save(obavijest);
        obavijestEventPublisher.publish("CREATED", saved);
        return ObavijestResponse.from(saved);
    }

    @Transactional
    public ObavijestResponse markAsRead(Long id, Long korisnikId) {
        Obavijest obavijest = obavijestRepository.findByIdAndKorisnikId(id, korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Obavijest not found"));

        obavijest.markAsRead();
        Obavijest saved = obavijestRepository.save(obavijest);
        obavijestEventPublisher.publish("UPDATED", saved);
        return ObavijestResponse.from(saved);
    }
}
