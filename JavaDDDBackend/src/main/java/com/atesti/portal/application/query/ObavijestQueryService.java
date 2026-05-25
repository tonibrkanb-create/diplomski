package com.atesti.portal.application.query;

import com.atesti.portal.application.dto.query.ObavijestResponse;
import com.atesti.portal.domain.repository.ObavijestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ObavijestQueryService {

    private final ObavijestRepository obavijestRepository;

    public List<ObavijestResponse> getByKorisnik(Long korisnikId) {
        return obavijestRepository.findByKorisnikIdOrderByCreatedAtDesc(korisnikId).stream()
                .map(ObavijestResponse::from)
                .collect(Collectors.toList());
    }
}
