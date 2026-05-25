package com.atesti.service;

import com.atesti.dto.PonudaRequest;
import com.atesti.entity.Korisnik;
import com.atesti.entity.Ponuda;
import com.atesti.exception.BadRequestException;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.KorisnikRepository;
import com.atesti.repository.PonudaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KorisnikPonudeService {

    private final PonudaRepository ponudaRepository;
    private final KorisnikRepository korisnikRepository;

    public List<Ponuda> getByKorisnik(Long korisnikId) {
        return ponudaRepository.findByKorisnikIdOrderByCreatedAtDesc(korisnikId);
    }

    public Ponuda getById(Long id, Long korisnikId) {
        return ponudaRepository.findByIdAndKorisnikId(id, korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Ponuda not found"));
    }

    @Transactional
    public Ponuda create(Long korisnikId, PonudaRequest request) {
        if (request.getOpis() == null || request.getOpis().isBlank()) {
            throw new BadRequestException("Opis je obavezan");
        }

        Korisnik korisnik = korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));

        LocalDate zeljeniDatum = null;
        if (request.getZeljeniDatum() != null && !request.getZeljeniDatum().isBlank()) {
            zeljeniDatum = LocalDate.parse(request.getZeljeniDatum());
        }

        Ponuda ponuda = Ponuda.builder()
                .korisnik(korisnik)
                .opis(request.getOpis())
                .vrstaAtesta(request.getVrstaAtesta())
                .lokacija(request.getLokacija())
                .zeljeniDatum(zeljeniDatum)
                .status("nova")
                .build();

        return ponudaRepository.save(ponuda);
    }
}
