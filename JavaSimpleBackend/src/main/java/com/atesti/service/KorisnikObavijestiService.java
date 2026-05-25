package com.atesti.service;

import com.atesti.entity.Obavijest;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.ObavijestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KorisnikObavijestiService {

    private final ObavijestRepository obavijestRepository;

    public List<Obavijest> getByKorisnik(Long korisnikId) {
        return obavijestRepository.findByKorisnikIdOrderByCreatedAtDesc(korisnikId);
    }

    @Transactional
    public Obavijest markAsRead(Long id, Long korisnikId) {
        Obavijest obavijest = obavijestRepository.findByIdAndKorisnikId(id, korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Obavijest not found"));
        obavijest.setProcitana(true);
        return obavijestRepository.save(obavijest);
    }
}
