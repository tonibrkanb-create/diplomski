package com.atesti.service;

import com.atesti.dto.RadniNalogRequest;
import com.atesti.dto.UskoroIsticeResponse;
import com.atesti.entity.*;
import com.atesti.exception.BadRequestException;
import com.atesti.exception.ResourceNotFoundException;
import com.atesti.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RadniNaloziService {

    private final RadniNalogRepository radniNalogRepository;
    private final NaruciteljRepository naruciteljRepository;
    private final AktivnostRepository aktivnostRepository;
    private final UskoroIsticeRepository uskoroIsticeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public String getNextBrojNaloga() {
        long count = radniNalogRepository.count();
        long sequenceNumber = count + 1;

        while (true) {
            String candidate = String.format("RN%03d", sequenceNumber);
            Optional<RadniNalog> existing = radniNalogRepository.findByBrojNaloga(candidate);
            if (existing.isEmpty()) {
                return candidate;
            }
            sequenceNumber++;
        }
    }

    public List<UskoroIsticeResponse> getUskoroIstice(int days) {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(days);

        List<UskoroIstice> items = uskoroIsticeRepository
                .findByIsActiveTrueAndDatumIstekaBetweenOrderByDatumIstekaAsc(today, threshold);

        return items.stream().map(item -> UskoroIsticeResponse.builder()
                .id(item.getId())
                .narucitelj(item.getNarucitelj() != null ? item.getNarucitelj().getName() : null)
                .radniNalog(item.getRadniNalog() != null ? item.getRadniNalog().getBrojNaloga() : null)
                .aktivnost(item.getAktivnost())
                .datumIsteka(item.getDatumIsteka())
                .isActive(item.getIsActive())
                .build()
        ).collect(Collectors.toList());
    }

    public List<RadniNalog> getAllRadniNalozi() {
        return radniNalogRepository.findAll();
    }

    public RadniNalog getRadniNalogById(Long id) {
        return radniNalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Radni nalog not found"));
    }

    public List<RadniNalog> getRadniNaloziByNarucitelj(Long naruciteljId) {
        return radniNalogRepository.findByNaruciteljId(naruciteljId);
    }

    @Transactional
    public RadniNalog createRadniNalog(RadniNalogRequest request) {
        if (request.getAktivnosti() == null) {
            throw new BadRequestException("Error creating radni nalog: Aktivnosti field is required and must be an array");
        }

        List<Long> aktivnostiList = normalizeAktivnostiInput(request.getAktivnosti());

        Long naruciteljId = request.getNaruciteljId() != null ? request.getNaruciteljId() : request.getNaruciteljIdAlt();
        Narucitelj narucitelj = naruciteljRepository.findById(naruciteljId)
                .orElseThrow(() -> new BadRequestException("Error creating radni nalog: Narucitelj not found"));

        String brojNaloga = request.getBrojNaloga();
        if (brojNaloga == null || brojNaloga.isBlank()) {
            brojNaloga = getNextBrojNaloga();
        }

        if (!brojNaloga.matches("^RN\\d{3}$")) {
            throw new BadRequestException("Error creating radni nalog: Broj naloga must be in format RN followed by 3 digits, e.g. RN001");
        }

        validateAktivnosti(aktivnostiList);

        RadniNalog nalog = RadniNalog.builder()
                .brojNaloga(brojNaloga)
                .narucitelj(narucitelj)
                .datum(parseDatum(request.getDatum()))
                .objekt(request.getObjekt())
                .fakturirano(request.getFakturirano() != null ? request.getFakturirano() : false)
                .zavrseno(request.getZavrseno() != null ? request.getZavrseno() : false)
                .opis(request.getOpis())
                .brojPonude(request.getBrojPonude())
                .brojRacuna(request.getBrojRacuna())
                .narudzbenica(request.getNarudzbenica())
                .ugovor(request.getUgovor())
                .aktivnosti(serializeAktivnosti(aktivnostiList))
                .pdfUrl(request.getPdfUrl())
                .build();

        nalog = radniNalogRepository.save(nalog);
        replaceUskoroIsticeEntries(nalog, aktivnostiList);

        return nalog;
    }

    @Transactional
    public RadniNalog updateRadniNalog(Long id, RadniNalogRequest request) {
        RadniNalog nalog = radniNalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Error updating radni nalog: Radni nalog not found"));

        if (request.getAktivnosti() == null) {
            throw new BadRequestException("Error updating radni nalog: Aktivnosti field is required and must be an array");
        }

        List<Long> aktivnostiList = normalizeAktivnostiInput(request.getAktivnosti());

        Long nextNaruciteljId = request.getNaruciteljIdAlt() != null ? request.getNaruciteljIdAlt()
                : (request.getNaruciteljId() != null ? request.getNaruciteljId() : nalog.getNaruciteljId());

        if (nextNaruciteljId != null) {
            Narucitelj narucitelj = naruciteljRepository.findById(nextNaruciteljId)
                    .orElseThrow(() -> new BadRequestException("Error updating radni nalog: Narucitelj not found"));
            nalog.setNarucitelj(narucitelj);
        }

        String brojNaloga = request.getBrojNaloga() != null ? request.getBrojNaloga() : nalog.getBrojNaloga();
        if (!brojNaloga.matches("^RN\\d{3}$")) {
            throw new BadRequestException("Error updating radni nalog: Broj naloga must be in format RN followed by 3 digits, e.g. RN001");
        }
        nalog.setBrojNaloga(brojNaloga);

        validateAktivnosti(aktivnostiList);

        if (request.getDatum() != null) nalog.setDatum(parseDatum(request.getDatum()));
        if (request.getObjekt() != null) nalog.setObjekt(request.getObjekt());
        if (request.getFakturirano() != null) nalog.setFakturirano(request.getFakturirano());
        if (request.getZavrseno() != null) nalog.setZavrseno(request.getZavrseno());
        if (request.getOpis() != null) nalog.setOpis(request.getOpis());
        if (request.getBrojPonude() != null) nalog.setBrojPonude(request.getBrojPonude());
        if (request.getBrojRacuna() != null) nalog.setBrojRacuna(request.getBrojRacuna());
        if (request.getNarudzbenica() != null) nalog.setNarudzbenica(request.getNarudzbenica());
        if (request.getUgovor() != null) nalog.setUgovor(request.getUgovor());
        if (request.getPdfUrl() != null) nalog.setPdfUrl(request.getPdfUrl());

        nalog.setAktivnosti(serializeAktivnosti(aktivnostiList));
        nalog = radniNalogRepository.save(nalog);

        replaceUskoroIsticeEntries(nalog, aktivnostiList);

        return nalog;
    }

    @Transactional
    public RadniNalog deleteRadniNalog(Long id) {
        RadniNalog nalog = radniNalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Error deleting radni nalog: Radni nalog not found"));

        if (!nalog.getBrojNaloga().matches("^RN\\d{3}$")) {
            throw new BadRequestException("Error deleting radni nalog: Broj naloga must be in format RN followed by 3 digits, e.g. RN001");
        }

        radniNalogRepository.delete(nalog);
        return nalog;
    }

    @Transactional
    public RadniNalog assignWorker(Long id, Long assignedUserId) {
        RadniNalog nalog = radniNalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Radni nalog not found"));

        if (assignedUserId == null) {
            nalog.setAssignedUser(null);
        } else {
            User user = userRepository.findById(assignedUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            nalog.setAssignedUser(user);
        }

        return radniNalogRepository.save(nalog);
    }

    private List<Long> normalizeAktivnostiInput(List<Object> aktivnostiInput) {
        if (aktivnostiInput == null) {
            return new ArrayList<>();
        }

        List<Long> sanitized = new ArrayList<>();
        for (Object item : aktivnostiInput) {
            Long parsedId = null;

            if (item instanceof Number) {
                long val = ((Number) item).longValue();
                if (val > 0) parsedId = val;
            } else if (item instanceof String) {
                String trimmed = ((String) item).trim();
                if (trimmed.matches("^\\d+$")) {
                    long val = Long.parseLong(trimmed);
                    if (val > 0) parsedId = val;
                }
            }

            if (parsedId == null) {
                throw new BadRequestException("Aktivnosti must be an array of numeric IDs");
            }

            if (!sanitized.contains(parsedId)) {
                sanitized.add(parsedId);
            }
        }

        return sanitized;
    }

    private void validateAktivnosti(List<Long> aktivnostiList) {
        if (aktivnostiList.isEmpty()) return;

        List<Aktivnost> found = aktivnostRepository.findByIdInAndIsActiveTrue(aktivnostiList);
        Set<Long> foundIds = found.stream().map(Aktivnost::getId).collect(Collectors.toSet());

        List<Long> missingIds = aktivnostiList.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());

        if (!missingIds.isEmpty()) {
            throw new BadRequestException("Aktivnosti IDs are invalid or inactive: " +
                    missingIds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }
    }

    private void replaceUskoroIsticeEntries(RadniNalog nalog, List<Long> aktivnostiList) {
        uskoroIsticeRepository.deleteByRadniNalogId(nalog.getId());

        if (aktivnostiList.isEmpty()) return;

        List<Aktivnost> aktivnostiConfig = aktivnostRepository.findByIdInAndIsActiveTrue(aktivnostiList);
        Map<Long, Aktivnost> configById = aktivnostiConfig.stream()
                .collect(Collectors.toMap(Aktivnost::getId, a -> a));

        for (Long aktivnostId : aktivnostiList) {
            Aktivnost config = configById.get(aktivnostId);
            if (config == null) continue;

            int rokTrajanjaMjeseci = config.getRokTrajanja();
            if (rokTrajanjaMjeseci <= 0) continue;

            LocalDate datumIsteka = nalog.getDatum().toLocalDate().plusMonths(rokTrajanjaMjeseci);

            UskoroIstice entry = UskoroIstice.builder()
                    .naruciteljId(nalog.getNaruciteljId())
                    .radniNalogId(nalog.getId())
                    .aktivnost(config.getAktivnost())
                    .datumIsteka(datumIsteka)
                    .isActive(true)
                    .build();

            uskoroIsticeRepository.save(entry);
        }
    }

    private String serializeAktivnosti(List<Long> aktivnostiList) {
        try {
            return objectMapper.writeValueAsString(aktivnostiList);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private LocalDateTime parseDatum(String datum) {
        if (datum == null) return LocalDateTime.now();
        try {
            return LocalDateTime.parse(datum);
        } catch (Exception e) {
            try {
                return LocalDate.parse(datum).atStartOfDay();
            } catch (Exception e2) {
                try {
                    return LocalDateTime.parse(datum, DateTimeFormatter.ISO_DATE_TIME);
                } catch (Exception e3) {
                    return LocalDateTime.now();
                }
            }
        }
    }
}
