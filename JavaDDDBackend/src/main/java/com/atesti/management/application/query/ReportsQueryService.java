package com.atesti.management.application.query;

import com.atesti.clients.domain.model.Narucitelj;
import com.atesti.clients.domain.repository.NaruciteljRepository;
import com.atesti.management.application.query.dto.NaloziReportItemResponse;
import com.atesti.management.application.query.dto.NaruciteljiReportItemResponse;
import com.atesti.staffidentity.domain.model.User;
import com.atesti.workorders.domain.model.RadniNalog;
import com.atesti.workorders.domain.repository.RadniNalogRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportsQueryService {

    private final RadniNalogRepository radniNalogRepository;
    private final NaruciteljRepository naruciteljRepository;
    private final ObjectMapper objectMapper;

    public List<NaloziReportItemResponse> getNaloziReport(String from, String to, String status) {
        List<RadniNalog> nalozi = radniNalogRepository.findAll();

        if (from != null && !from.isBlank()) {
            LocalDateTime fromDate = LocalDateTime.parse(from + "T00:00:00");
            nalozi = nalozi.stream().filter(n -> n.getDatum() != null && !n.getDatum().isBefore(fromDate)).collect(Collectors.toList());
        }
        if (to != null && !to.isBlank()) {
            LocalDateTime toDate = LocalDateTime.parse(to + "T23:59:59");
            nalozi = nalozi.stream().filter(n -> n.getDatum() != null && !n.getDatum().isAfter(toDate)).collect(Collectors.toList());
        }
        if (status != null && !status.isBlank()) {
            nalozi = nalozi.stream().filter(n -> n.deriveStatus().equals(status)).collect(Collectors.toList());
        }

        nalozi.sort((a, b) -> {
            if (a.getDatum() == null && b.getDatum() == null) return 0;
            if (a.getDatum() == null) return 1;
            if (b.getDatum() == null) return -1;
            return b.getDatum().compareTo(a.getDatum());
        });

        return nalozi.stream().map(this::toReportItem).collect(Collectors.toList());
    }

    public List<NaruciteljiReportItemResponse> getNaruciteljiReport() {
        List<Narucitelj> narucitelji = naruciteljRepository.findAll();
        narucitelji.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        List<RadniNalog> nalozi = radniNalogRepository.findAll();
        Map<Long, Long> countMap = new HashMap<>();
        for (RadniNalog n : nalozi) {
            if (n.getNaruciteljId() != null) {
                countMap.merge(n.getNaruciteljId(), 1L, Long::sum);
            }
        }

        return narucitelji.stream().map(n -> NaruciteljiReportItemResponse.builder()
                .id(n.getId())
                .naziv(n.getName())
                .adresa(n.getAdresa() != null ? n.getAdresa() : "-")
                .kontakt(n.getKontaktOsoba() != null ? n.getKontaktOsoba() : "-")
                .naloziCount(countMap.getOrDefault(n.getId(), 0L))
                .build()
        ).collect(Collectors.toList());
    }

    public List<NaloziReportItemResponse> getMyTasks(Long userId) {
        List<RadniNalog> nalozi = radniNalogRepository.findByAssignedUserIdOrderByDatumDesc(userId);
        return nalozi.stream().map(this::toReportItem).collect(Collectors.toList());
    }

    private NaloziReportItemResponse toReportItem(RadniNalog n) {
        List<Long> aktivnosti = parseAktivnostiJson(n.getAktivnosti());
        User assigned = n.getAssignedUser();
        String assignedName = null;
        if (assigned != null) {
            String name = ((assigned.getIme() != null ? assigned.getIme() : "") + " " +
                    (assigned.getPrezime() != null ? assigned.getPrezime() : "")).trim();
            assignedName = name.isEmpty() ? assigned.getUsername() : name;
        }

        return NaloziReportItemResponse.builder()
                .id(n.getId())
                .datum(n.getDatum() != null ? n.getDatum().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .status(n.deriveStatus())
                .naruciteljNaziv(n.getNarucitelj() != null ? n.getNarucitelj().getName() : "-")
                .assignedUser(assignedName)
                .aktivnostiCount(aktivnosti.size())
                .build();
    }

    private List<Long> parseAktivnostiJson(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
