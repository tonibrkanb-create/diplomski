package com.atesti.portal.application.query;

import com.atesti.portal.application.dto.query.PonudaResponse;
import com.atesti.portal.domain.repository.PonudaRepository;
import com.atesti.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PonudaQueryService {

    private final PonudaRepository ponudaRepository;

    public List<PonudaResponse> getByKorisnik(Long korisnikId) {
        return ponudaRepository.findByKorisnikIdOrderByCreatedAtDesc(korisnikId).stream()
                .map(PonudaResponse::from)
                .collect(Collectors.toList());
    }

    public PonudaResponse getById(Long id, Long korisnikId) {
        return ponudaRepository.findByIdAndKorisnikId(id, korisnikId)
                .map(PonudaResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Ponuda not found"));
    }

    public List<PonudaResponse> getAll() {
        return ponudaRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(PonudaResponse::from)
                .collect(Collectors.toList());
    }

    public PonudaResponse getByIdAdmin(Long id) {
        return ponudaRepository.findById(id)
                .map(PonudaResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Ponuda not found"));
    }
}
