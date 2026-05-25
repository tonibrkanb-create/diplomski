package com.atesti.portal.application.command;

import com.atesti.portal.application.dto.command.CreateRecenzijaCommand;
import com.atesti.portal.application.dto.command.RespondRecenzijaCommand;
import com.atesti.portal.application.dto.query.RecenzijaResponse;
import com.atesti.portal.domain.model.Korisnik;
import com.atesti.portal.domain.model.Recenzija;
import com.atesti.portal.domain.repository.KorisnikRepository;
import com.atesti.portal.domain.repository.RecenzijaRepository;
import com.atesti.shared.exception.ResourceNotFoundException;
import com.atesti.workorders.domain.model.RadniNalog;
import com.atesti.workorders.domain.repository.RadniNalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecenzijaCommandService {

    private final RecenzijaRepository recenzijaRepository;
    private final KorisnikRepository korisnikRepository;
    private final RadniNalogRepository radniNalogRepository;

    @Transactional
    public RecenzijaResponse create(Long korisnikId, CreateRecenzijaCommand command) {
        Korisnik korisnik = korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));

        RadniNalog radniNalog = null;
        if (command.getRadniNalogId() != null) {
            radniNalog = radniNalogRepository.findById(command.getRadniNalogId()).orElse(null);
        }

        Recenzija recenzija = Recenzija.create(korisnik, command.getOcjena(), command.getKomentar(), radniNalog);
        Recenzija saved = recenzijaRepository.save(recenzija);
        return RecenzijaResponse.from(saved);
    }

    @Transactional
    public RecenzijaResponse respond(Long id, RespondRecenzijaCommand command) {
        Recenzija recenzija = recenzijaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recenzija not found"));

        recenzija.respond(command.getOdgovor());
        Recenzija saved = recenzijaRepository.save(recenzija);
        return RecenzijaResponse.from(saved);
    }
}
