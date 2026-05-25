package com.atesti.service;

import com.atesti.dto.SendObavijestRequest;
import com.atesti.entity.Korisnik;
import com.atesti.entity.Obavijest;
import com.atesti.exception.BadRequestException;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.KorisnikRepository;
import com.atesti.repository.ObavijestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObavijestiManagementService {

    private final ObavijestRepository obavijestRepository;
    private final KorisnikRepository korisnikRepository;

    @Transactional
    public Obavijest create(SendObavijestRequest request) {
        if (request.getKorisnikId() == null || request.getNaslov() == null || request.getPoruka() == null) {
            throw new BadRequestException("Sva polja su obavezna");
        }

        Korisnik korisnik = korisnikRepository.findById(request.getKorisnikId())
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));

        Obavijest obavijest = Obavijest.builder()
                .korisnik(korisnik)
                .naslov(request.getNaslov())
                .poruka(request.getPoruka())
                .procitana(false)
                .build();

        return obavijestRepository.save(obavijest);
    }

    public List<Korisnik> getAllKorisnici() {
        return korisnikRepository.findByIsActiveTrueOrderByImeAsc();
    }
}
