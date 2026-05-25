package com.atesti.portal.application.query;

import com.atesti.portal.application.dto.RecenzijaResponse;
import com.atesti.portal.domain.repository.RecenzijaRepository;
import com.atesti.portal.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecenzijaQueryService {

    private final RecenzijaRepository recenzijaRepository;

    public List<RecenzijaResponse> getByKorisnik(Long korisnikId) {
        return recenzijaRepository.findByKorisnikIdOrderByCreatedAtDesc(korisnikId).stream()
                .map(RecenzijaResponse::from)
                .collect(Collectors.toList());
    }

    public RecenzijaResponse getById(Long id, Long korisnikId) {
        return recenzijaRepository.findByIdAndKorisnikId(id, korisnikId)
                .map(RecenzijaResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Recenzija not found"));
    }

    public List<RecenzijaResponse> getAll() {
        return recenzijaRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(RecenzijaResponse::from)
                .collect(Collectors.toList());
    }
}
