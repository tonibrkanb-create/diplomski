package com.atesti.portal.application.command;

import com.atesti.portal.application.dto.CreateRecenzijaCommand;
import com.atesti.portal.application.dto.RespondRecenzijaCommand;
import com.atesti.portal.application.dto.RecenzijaResponse;
import com.atesti.portal.domain.model.Korisnik;
import com.atesti.portal.domain.model.Recenzija;
import com.atesti.portal.domain.repository.KorisnikRepository;
import com.atesti.portal.domain.repository.RecenzijaRepository;
import com.atesti.portal.exception.ResourceNotFoundException;
import com.atesti.portal.infrastructure.kafka.RecenzijaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecenzijaCommandService {

    private final RecenzijaRepository recenzijaRepository;
    private final KorisnikRepository korisnikRepository;
    private final RecenzijaEventPublisher recenzijaEventPublisher;

    @Transactional
    public RecenzijaResponse create(Long korisnikId, CreateRecenzijaCommand command) {
        Korisnik korisnik = korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));

        Recenzija recenzija = Recenzija.create(korisnik, command.getOcjena(), command.getKomentar(), command.getRadniNalogId());
        Recenzija saved = recenzijaRepository.save(recenzija);
        recenzijaEventPublisher.publish("CREATED", saved);
        return RecenzijaResponse.from(saved);
    }

    @Transactional
    public RecenzijaResponse respond(Long id, RespondRecenzijaCommand command) {
        Recenzija recenzija = recenzijaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recenzija not found"));

        recenzija.respond(command.getOdgovor());
        Recenzija saved = recenzijaRepository.save(recenzija);
        recenzijaEventPublisher.publish("UPDATED", saved);
        return RecenzijaResponse.from(saved);
    }
}
