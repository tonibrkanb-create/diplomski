package com.atesti.service;

import com.atesti.dto.UpdatePonudaStatusRequest;
import com.atesti.entity.Ponuda;
import com.atesti.exception.BadRequestException;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.PonudaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PonudeManagementService {

    private final PonudaRepository ponudaRepository;

    private static final Set<String> VALID_STATUSES = Set.of("nova", "poslana", "odobrena", "odbijena");

    public List<Ponuda> getAll() {
        return ponudaRepository.findAllByOrderByCreatedAtDesc();
    }

    public Ponuda getById(Long id) {
        return ponudaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ponuda not found"));
    }

    @Transactional
    public Ponuda updateStatus(Long id, UpdatePonudaStatusRequest request) {
        Ponuda ponuda = ponudaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ponuda not found"));

        if (request.getStatus() != null) {
            if (!VALID_STATUSES.contains(request.getStatus())) {
                throw new BadRequestException("Nevažeći status: " + request.getStatus());
            }
            ponuda.setStatus(request.getStatus());
        }
        if (request.getOdgovor() != null) {
            ponuda.setOdgovor(request.getOdgovor());
        }

        return ponudaRepository.save(ponuda);
    }
}
