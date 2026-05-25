package com.atesti.service;

import com.atesti.dto.RespondRecenzijaRequest;
import com.atesti.entity.Recenzija;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.RecenzijaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecenzijeManagementService {

    private final RecenzijaRepository recenzijaRepository;

    public List<Recenzija> getAll() {
        return recenzijaRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Recenzija respond(Long id, RespondRecenzijaRequest request) {
        Recenzija recenzija = recenzijaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recenzija not found"));
        recenzija.setOdgovor(request.getOdgovor());
        return recenzijaRepository.save(recenzija);
    }
}
