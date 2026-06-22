package com.atesti.workorders.application.command;

import com.atesti.catalogue.domain.model.Aktivnost;
import com.atesti.catalogue.domain.repository.AktivnostRepository;
import com.atesti.clients.domain.model.Narucitelj;
import com.atesti.clients.domain.repository.NaruciteljRepository;
import com.atesti.shared.exception.BadRequestException;
import com.atesti.shared.exception.ResourceNotFoundException;
import com.atesti.staffidentity.domain.model.User;
import com.atesti.staffidentity.domain.repository.UserRepository;
import com.atesti.workorders.application.dto.command.AssignWorkerCommand;
import com.atesti.workorders.application.dto.command.CreateRadniNalogCommand;
import com.atesti.workorders.application.dto.command.UpdateRadniNalogCommand;
import com.atesti.workorders.application.dto.query.RadniNalogResponse;
import com.atesti.workorders.domain.model.RadniNalogProjection;
import com.atesti.workorders.domain.persistance.RadniNalogEntity;
import com.atesti.workorders.domain.model.UskoroIstice;
import com.atesti.workorders.domain.repository.RadniNalogRepository;
import com.atesti.workorders.domain.repository.RadniNalogWriteRepository;
import com.atesti.workorders.domain.repository.UskoroIsticeRepository;
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
public class RadniNalogDomainService {

    private final RadniNalogWriteRepository radniNalogRepository;
    private final NaruciteljRepository naruciteljRepository;
    private final AktivnostRepository aktivnostRepository;
    private final UskoroIsticeRepository uskoroIsticeRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public RadniNalogResponse create(CreateRadniNalogCommand command) {

        List<Long> aktivnostiList = normalizeAktivnostiInput(command.aktivnosti());

        Narucitelj narucitelj = naruciteljRepository.findById(command.naruciteljId())
                .orElseThrow(() -> new BadRequestException(
                        "Error creating radni nalog: Narucitelj not found"
                ));

        validateAktivnosti(aktivnostiList);

        RadniNalogEntity nalog = new RadniNalogEntity(
                command.brojNaloga(),
                command.datum(),
                command.objekt(),
                command.fakturirano(),
                command.zavrseno(),
                command.opis(),
                command.brojPonude(),
                command.brojRacuna(),
                command.narudzbenica(),
                command.ugovor(),
                "",
                command.pdfUrl(),
                narucitelj,
                new User()
        );

        nalog.setAktivnostiIds(aktivnostiList, objectMapper);

        if (command.assignedUserId() != null) {
            User user = userRepository.findById(command.assignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            nalog.assignTo(user);
        }

        nalog = radniNalogRepository.save(nalog);

        replaceUskoroIsticeEntries(nalog, aktivnostiList);

        return RadniNalogResponse.from(new RadniNalogProjection(nalog.getId(), nalog.getBrojNaloga(), nalog.getNaruciteljId(), nalog.getFakturirano(), nalog.getZavrseno()));
    }

    @Transactional
    public RadniNalogResponse update(Long id, UpdateRadniNalogCommand command) {

        RadniNalogEntity nalog = radniNalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Error updating radni nalog: Radni nalog not found"
                ));

        List<Long> aktivnostiList = normalizeAktivnostiInput(command.aktivnosti());

        Long nextNaruciteljId = command.naruciteljIdAlt() != null
                ? command.naruciteljIdAlt()
                : command.naruciteljId();

        if (nextNaruciteljId != null) {
            Narucitelj narucitelj = naruciteljRepository.findById(nextNaruciteljId)
                    .orElseThrow(() -> new BadRequestException(
                            "Error updating radni nalog: Narucitelj not found"
                    ));

            nalog.setNarucitelj(narucitelj);
        }

        String brojNaloga = command.brojNaloga() != null
                ? command.brojNaloga()
                : nalog.getBrojNaloga();

        validateAktivnosti(aktivnostiList);

        nalog.updateDetails(
                brojNaloga,
                command.datum() != null ? parseDatum(command.datum()) : null,
                command.objekt(),
                command.fakturirano(),
                command.zavrseno(),
                command.opis(),
                command.brojPonude(),
                command.brojRacuna(),
                command.narudzbenica(),
                command.ugovor(),
                command.pdfUrl()
        );

        nalog.setAktivnostiIds(aktivnostiList, objectMapper);

        if (command.assignedUserId() != null) {
            User user = userRepository.findById(command.assignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            nalog.assignTo(user);
        }

        nalog = radniNalogRepository.save(nalog);

        replaceUskoroIsticeEntries(nalog, aktivnostiList);

        return RadniNalogResponse.from(new RadniNalogProjection(nalog.getId(), nalog.getBrojNaloga(), nalog.getNaruciteljId(), nalog.getFakturirano(), nalog.getZavrseno()));
    }

    @Transactional
    public RadniNalogResponse delete(Long id) {

        RadniNalogEntity nalog = radniNalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Error deleting radni nalog: Radni nalog not found"
                ));

        var response = new RadniNalogProjection(nalog.getId(), nalog.getBrojNaloga(), nalog.getNaruciteljId(), nalog.getFakturirano(), nalog.getZavrseno());


        radniNalogRepository.delete(nalog.getId());

        return RadniNalogResponse.from(response);
    }

    @Transactional
    public RadniNalogResponse assignWorker(Long id, AssignWorkerCommand command) {

        RadniNalogEntity nalog = radniNalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Radni nalog not found"
                ));

        if (command.assignedUserId() == null) {
            nalog.assignTo(null);
        } else {
            User user = userRepository.findById(command.assignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "User not found"
                    ));

            nalog.assignTo(user);
        }

        nalog = radniNalogRepository.save(nalog);

        return RadniNalogResponse.from(new RadniNalogProjection(nalog.getId(), nalog.getBrojNaloga(), nalog.getNaruciteljId(), nalog.getFakturirano(), nalog.getZavrseno()));
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

        List<Long> missingIds = aktivnostiList.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());

        if (!missingIds.isEmpty()) {
            throw new BadRequestException("Aktivnosti IDs are invalid or inactive: " + missingIds.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }
    }

    private void replaceUskoroIsticeEntries(RadniNalogEntity nalog, List<Long> aktivnostiList) {
        uskoroIsticeRepository.deleteByRadniNalogId(nalog.getId());

        if (aktivnostiList.isEmpty()) return;

        List<Aktivnost> aktivnostiConfig = aktivnostRepository.findByIdInAndIsActiveTrue(aktivnostiList);
        Map<Long, Aktivnost> configById = aktivnostiConfig.stream().collect(Collectors.toMap(Aktivnost::getId, a -> a));

        for (Long aktivnostId : aktivnostiList) {
            Aktivnost config = configById.get(aktivnostId);
            if (config == null) continue;

            int rokTrajanjaMjeseci = config.getRokTrajanja();
            if (rokTrajanjaMjeseci <= 0) continue;

            LocalDate datumIsteka = nalog.getDatum().toLocalDate().plusMonths(rokTrajanjaMjeseci);

            UskoroIstice entry = UskoroIstice.builder().naruciteljId(nalog.getNaruciteljId()).radniNalogId(nalog.getId()).aktivnost(config.getAktivnost()).datumIsteka(datumIsteka).isActive(true).build();

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
