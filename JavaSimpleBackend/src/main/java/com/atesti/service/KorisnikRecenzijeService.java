package com.atesti.service;

import com.atesti.dto.RecenzijaRequest;
import com.atesti.entity.Korisnik;
import com.atesti.entity.Recenzija;
import com.atesti.exception.BadRequestException;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.KorisnikRepository;
import com.atesti.repository.RecenzijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KorisnikRecenzijeService {

    private final RecenzijaRepository recenzijaRepository;
    private final KorisnikRepository korisnikRepository;

    public List<Recenzija> getByKorisnik(Long korisnikId) {
        return recenzijaRepository.findByKorisnikIdOrderByCreatedAtDesc(korisnikId);
    }

    public Recenzija getById(Long id, Long korisnikId) {
        return recenzijaRepository.findByIdAndKorisnikId(id, korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Recenzija not found"));
    }

    @Transactional
    public Recenzija create(Long korisnikId, RecenzijaRequest request) {
        if (request.getOcjena() == null || request.getOcjena() < 1 || request.getOcjena() > 5) {
            throw new BadRequestException("Ocjena mora biti između 1 i 5");
        }

        Korisnik korisnik = korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));

        Recenzija recenzija = Recenzija.builder()
                .korisnik(korisnik)
                .ocjena(request.getOcjena())
                .komentar(request.getKomentar())
                .build();

        if (request.getRadniNalogId() != null) {
            recenzija.setRadniNalogId(request.getRadniNalogId());
        }

        return recenzijaRepository.save(recenzija);
    }
}
