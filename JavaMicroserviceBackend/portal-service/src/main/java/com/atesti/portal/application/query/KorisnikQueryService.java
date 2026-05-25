package com.atesti.portal.application.query;

import com.atesti.portal.application.dto.KorisnikProfileResponse;
import com.atesti.portal.domain.model.Korisnik;
import com.atesti.portal.domain.repository.KorisnikRepository;
import com.atesti.portal.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KorisnikQueryService {

    private final KorisnikRepository korisnikRepository;

    public KorisnikProfileResponse getProfile(Long korisnikId) {
        Korisnik korisnik = korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik not found"));
        return KorisnikProfileResponse.from(korisnik);
    }

    public List<KorisnikProfileResponse> getAllActive() {
        return korisnikRepository.findByIsActiveTrueOrderByImeAsc().stream()
                .map(KorisnikProfileResponse::from)
                .collect(Collectors.toList());
    }
}
