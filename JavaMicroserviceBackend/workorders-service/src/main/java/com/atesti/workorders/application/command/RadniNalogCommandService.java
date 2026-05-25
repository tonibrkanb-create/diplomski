package com.atesti.workorders.application.command;

import com.atesti.workorders.application.dto.command.CreateRadniNalogCommand;
import com.atesti.workorders.application.dto.command.UpdateRadniNalogCommand;
import com.atesti.workorders.application.dto.query.RadniNalogResponse;
import com.atesti.workorders.domain.model.RadniNalog;
import com.atesti.workorders.domain.model.UskoroIstice;
import com.atesti.workorders.domain.repository.RadniNalogRepository;
import com.atesti.workorders.domain.repository.UskoroIsticeRepository;
import com.atesti.workorders.exception.BadRequestException;
import com.atesti.workorders.exception.ResourceNotFoundException;
import com.atesti.workorders.infrastructure.kafka.RadniNalogEventPublisher;
import com.atesti.workorders.projection.model.LocalAktivnost;
import com.atesti.workorders.projection.model.LocalNarucitelj;
import com.atesti.workorders.projection.model.LocalUser;
import com.atesti.workorders.projection.repository.LocalAktivnostRepository;
import com.atesti.workorders.projection.repository.LocalNaruciteljRepository;
import com.atesti.workorders.projection.repository.LocalUserRepository;
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
public class RadniNalogCommandService {

    private final RadniNalogRepository radniNalogRepository;
    private final LocalNaruciteljRepository localNaruciteljRepository;
    private final LocalAktivnostRepository localAktivnostRepository;
    private final UskoroIsticeRepository uskoroIsticeRepository;
    private final LocalUserRepository localUserRepository;
    private final RadniNalogEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public RadniNalogResponse create(CreateRadniNalogCommand command) {
        if (command.getAktivnosti() == null) {
            throw new BadRequestException("Error creating radni nalog: Aktivnosti field is required and must be an array");
        }

        List<Long> aktivnostiList = normalizeAktivnostiInput(command.getAktivnosti());

        Long naruciteljId = command.getNaruciteljId() != null ? command.getNaruciteljId() : command.getNaruciteljIdAlt();
        LocalNarucitelj narucitelj = localNaruciteljRepository.findById(naruciteljId)
                .orElseThrow(() -> new BadRequestException("Error creating radni nalog: Narucitelj not found"));

        String brojNaloga = command.getBrojNaloga();
        if (brojNaloga == null || brojNaloga.isBlank()) {
            brojNaloga = getNextBrojNaloga();
        }

        if (!brojNaloga.matches("^RN\\d{3}$")) {
            throw new BadRequestException("Error creating radni nalog: Broj naloga must be in format RN followed by 3 digits, e.g. RN001");
        }

        validateAktivnosti(aktivnostiList);

        RadniNalog nalog = RadniNalog.builder()
                .brojNaloga(brojNaloga)
                .naruciteljId(naruciteljId)
                .datum(parseDatum(command.getDatum()))
                .objekt(command.getObjekt())
                .fakturirano(command.getFakturirano() != null ? command.getFakturirano() : false)
                .zavrseno(command.getZavrseno() != null ? command.getZavrseno() : false)
                .opis(command.getOpis())
                .brojPonude(command.getBrojPonude())
                .brojRacuna(command.getBrojRacuna())
                .narudzbenica(command.getNarudzbenica())
                .ugovor(command.getUgovor())
                .pdfUrl(command.getPdfUrl())
                .build();

        if (command.getAssignedUserId() != null) {
            localUserRepository.findById(command.getAssignedUserId())
                    .orElseThrow(() -> new BadRequestException("Error creating radni nalog: Assigned user not found"));
            nalog.assignTo(command.getAssignedUserId());
        }

        nalog.setAktivnostiIds(aktivnostiList, objectMapper);
        nalog = radniNalogRepository.save(nalog);
        replaceUskoroIsticeEntries(nalog, aktivnostiList);

        eventPublisher.publish("CREATED", nalog);

        LocalUser user = nalog.getAssignedUserId() != null
                ? localUserRepository.findById(nalog.getAssignedUserId()).orElse(null) : null;
        return RadniNalogResponse.from(nalog, narucitelj, user);
    }

    @Transactional
    public RadniNalogResponse update(Long id, UpdateRadniNalogCommand command) {
        RadniNalog nalog = radniNalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Error updating radni nalog: Radni nalog not found"));

        if (command.getAktivnosti() == null) {
            throw new BadRequestException("Error updating radni nalog: Aktivnosti field is required and must be an array");
        }

        List<Long> aktivnostiList = normalizeAktivnostiInput(command.getAktivnosti());

        Long nextNaruciteljId = command.getNaruciteljIdAlt() != null ? command.getNaruciteljIdAlt()
                : (command.getNaruciteljId() != null ? command.getNaruciteljId() : nalog.getNaruciteljId());

        if (nextNaruciteljId != null) {
            localNaruciteljRepository.findById(nextNaruciteljId)
                    .orElseThrow(() -> new BadRequestException("Error updating radni nalog: Narucitelj not found"));
            nalog.setNaruciteljId(nextNaruciteljId);
        }

        String brojNaloga = command.getBrojNaloga() != null ? command.getBrojNaloga() : nalog.getBrojNaloga();
        if (!brojNaloga.matches("^RN\\d{3}$")) {
            throw new BadRequestException("Error updating radni nalog: Broj naloga must be in format RN followed by 3 digits, e.g. RN001");
        }

        validateAktivnosti(aktivnostiList);

        nalog.updateDetails(
                brojNaloga,
                command.getDatum() != null ? parseDatum(command.getDatum()) : null,
                command.getObjekt(),
                command.getFakturirano(),
                command.getZavrseno(),
                command.getOpis(),
                command.getBrojPonude(),
                command.getBrojRacuna(),
                command.getNarudzbenica(),
                command.getUgovor(),
                command.getPdfUrl()
        );

        if (command.getAssignedUserId() != null) {
            localUserRepository.findById(command.getAssignedUserId())
                    .orElseThrow(() -> new BadRequestException("Error updating radni nalog: Assigned user not found"));
            nalog.assignTo(command.getAssignedUserId());
        }

        nalog.setAktivnostiIds(aktivnostiList, objectMapper);
        nalog = radniNalogRepository.save(nalog);
        replaceUskoroIsticeEntries(nalog, aktivnostiList);

        eventPublisher.publish("UPDATED", nalog);

        LocalNarucitelj narucitelj = nalog.getNaruciteljId() != null
                ? localNaruciteljRepository.findById(nalog.getNaruciteljId()).orElse(null) : null;
        LocalUser user = nalog.getAssignedUserId() != null
                ? localUserRepository.findById(nalog.getAssignedUserId()).orElse(null) : null;
        return RadniNalogResponse.from(nalog, narucitelj, user);
    }

    @Transactional
    public RadniNalogResponse delete(Long id) {
        RadniNalog nalog = radniNalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Error deleting radni nalog: Radni nalog not found"));

        if (!nalog.getBrojNaloga().matches("^RN\\d{3}$")) {
            throw new BadRequestException("Error deleting radni nalog: Broj naloga must be in format RN followed by 3 digits, e.g. RN001");
        }

        LocalNarucitelj narucitelj = nalog.getNaruciteljId() != null
                ? localNaruciteljRepository.findById(nalog.getNaruciteljId()).orElse(null) : null;
        LocalUser user = nalog.getAssignedUserId() != null
                ? localUserRepository.findById(nalog.getAssignedUserId()).orElse(null) : null;
        RadniNalogResponse response = RadniNalogResponse.from(nalog, narucitelj, user);

        radniNalogRepository.delete(nalog);
        eventPublisher.publish("DELETED", nalog);

        return response;
    }

    @Transactional
    public RadniNalogResponse assignWorker(Long id, Long assignedUserId) {
        RadniNalog nalog = radniNalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Radni nalog not found"));

        if (assignedUserId == null) {
            nalog.assignTo(null);
        } else {
            localUserRepository.findById(assignedUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            nalog.assignTo(assignedUserId);
        }

        nalog = radniNalogRepository.save(nalog);
        eventPublisher.publish("UPDATED", nalog);

        LocalNarucitelj narucitelj = nalog.getNaruciteljId() != null
                ? localNaruciteljRepository.findById(nalog.getNaruciteljId()).orElse(null) : null;
        LocalUser user = nalog.getAssignedUserId() != null
                ? localUserRepository.findById(nalog.getAssignedUserId()).orElse(null) : null;
        return RadniNalogResponse.from(nalog, narucitelj, user);
    }

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

        List<LocalAktivnost> found = localAktivnostRepository.findByIdInAndIsActiveTrue(aktivnostiList);
        Set<Long> foundIds = found.stream().map(LocalAktivnost::getId).collect(Collectors.toSet());

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

        List<LocalAktivnost> aktivnostiConfig = localAktivnostRepository.findByIdInAndIsActiveTrue(aktivnostiList);
        Map<Long, LocalAktivnost> configById = aktivnostiConfig.stream()
                .collect(Collectors.toMap(LocalAktivnost::getId, a -> a));

        LocalNarucitelj narucitelj = nalog.getNaruciteljId() != null
                ? localNaruciteljRepository.findById(nalog.getNaruciteljId()).orElse(null) : null;

        for (Long aktivnostId : aktivnostiList) {
            LocalAktivnost config = configById.get(aktivnostId);
            if (config == null) continue;

            Integer rokTrajanjaMjeseci = config.getRokTrajanja();
            if (rokTrajanjaMjeseci == null || rokTrajanjaMjeseci <= 0) continue;

            LocalDate datumIsteka = nalog.getDatum().toLocalDate().plusMonths(rokTrajanjaMjeseci);

            UskoroIstice entry = UskoroIstice.builder()
                    .naruciteljId(nalog.getNaruciteljId())
                    .radniNalogId(nalog.getId())
                    .aktivnostId(aktivnostId)
                    .aktivnost(config.getAktivnost())
                    .naruciteljName(narucitelj != null ? narucitelj.getName() : null)
                    .radniNalogBrojNaloga(nalog.getBrojNaloga())
                    .aktivnostName(config.getAktivnost())
                    .datumIsteka(datumIsteka)
                    .isActive(true)
                    .build();

            uskoroIsticeRepository.save(entry);
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
