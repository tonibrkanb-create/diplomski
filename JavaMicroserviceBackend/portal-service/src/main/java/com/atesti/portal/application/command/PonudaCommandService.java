package com.atesti.portal.application.command;

import com.atesti.portal.application.dto.CreatePonudaCommand;
import com.atesti.portal.application.dto.UpdatePonudaStatusCommand;
import com.atesti.portal.application.dto.PonudaResponse;
import com.atesti.portal.domain.model.Korisnik;
import com.atesti.portal.domain.model.Ponuda;
import com.atesti.portal.domain.repository.KorisnikRepository;
import com.atesti.portal.domain.repository.PonudaRepository;
import com.atesti.portal.exception.BadRequestException;
import com.atesti.portal.exception.ResourceNotFoundException;
import com.atesti.portal.infrastructure.kafka.PonudaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PonudaCommandService {

    private final PonudaRepository ponudaRepository;
    private final KorisnikRepository korisnikRepository;
    private final PonudaEventPublisher ponudaEventPublisher;

    @Transactional
    public PonudaResponse create(Long korisnikId, CreatePonudaCommand command) {
        if (command.getOpis() == null || command.getOpis().isBlank()) {
            throw new BadRequestException("Opis je obavezan");
        }

        Korisnik korisnik = korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));

        LocalDate zeljeniDatum = null;
        if (command.getZeljeniDatum() != null && !command.getZeljeniDatum().isBlank()) {
            zeljeniDatum = LocalDate.parse(command.getZeljeniDatum());
        }

        Ponuda ponuda = Ponuda.builder()
                .korisnik(korisnik)
                .opis(command.getOpis())
                .vrstaAtesta(command.getVrstaAtesta())
                .lokacija(command.getLokacija())
                .zeljeniDatum(zeljeniDatum)
                .status("nova")
                .build();

        Ponuda saved = ponudaRepository.save(ponuda);
        ponudaEventPublisher.publish("CREATED", saved);
        return PonudaResponse.from(saved);
    }

    @Transactional
    public PonudaResponse updateStatus(Long id, UpdatePonudaStatusCommand command) {
        Ponuda ponuda = ponudaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ponuda not found"));

        ponuda.updateStatus(command.getStatus(), command.getOdgovor());
        Ponuda saved = ponudaRepository.save(ponuda);
        ponudaEventPublisher.publish("UPDATED", saved);
        return PonudaResponse.from(saved);
    }
}
