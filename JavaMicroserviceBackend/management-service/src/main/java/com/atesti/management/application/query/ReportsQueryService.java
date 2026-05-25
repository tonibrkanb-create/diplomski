package com.atesti.management.application.query;

import com.atesti.management.application.query.dto.NaloziReportItemResponse;
import com.atesti.management.application.query.dto.NaruciteljiReportItemResponse;
import com.atesti.management.projection.model.LocalNarucitelj;
import com.atesti.management.projection.model.LocalRadniNalog;
import com.atesti.management.projection.model.LocalUser;
import com.atesti.management.projection.repository.LocalNaruciteljRepository;
import com.atesti.management.projection.repository.LocalRadniNalogRepository;
import com.atesti.management.projection.repository.LocalUserRepository;
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

    private final LocalRadniNalogRepository radniNalogRepository;
    private final LocalNaruciteljRepository naruciteljRepository;
    private final LocalUserRepository userRepository;
    private final ObjectMapper objectMapper;

    public List<NaloziReportItemResponse> getNaloziReport(String from, String to, String status) {
        List<LocalRadniNalog> nalozi = radniNalogRepository.findAll();

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

        Map<Long, LocalNarucitelj> naruciteljiMap = naruciteljRepository.findAll().stream()
                .collect(Collectors.toMap(LocalNarucitelj::getId, n -> n, (a, b) -> a));
        Map<Long, LocalUser> usersMap = userRepository.findAll().stream()
                .collect(Collectors.toMap(LocalUser::getId, u -> u, (a, b) -> a));

        return nalozi.stream().map(n -> toReportItem(n, naruciteljiMap, usersMap)).collect(Collectors.toList());
    }

    public List<NaruciteljiReportItemResponse> getNaruciteljiReport() {
        List<LocalNarucitelj> narucitelji = naruciteljRepository.findAll();
        narucitelji.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        List<LocalRadniNalog> nalozi = radniNalogRepository.findAll();
        Map<Long, Long> countMap = new HashMap<>();
        for (LocalRadniNalog n : nalozi) {
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
        List<LocalRadniNalog> nalozi = radniNalogRepository.findByAssignedUserIdOrderByDatumDesc(userId);

        Map<Long, LocalNarucitelj> naruciteljiMap = naruciteljRepository.findAll().stream()
                .collect(Collectors.toMap(LocalNarucitelj::getId, n -> n, (a, b) -> a));
        Map<Long, LocalUser> usersMap = userRepository.findAll().stream()
                .collect(Collectors.toMap(LocalUser::getId, u -> u, (a, b) -> a));

        return nalozi.stream().map(n -> toReportItem(n, naruciteljiMap, usersMap)).collect(Collectors.toList());
    }

    private NaloziReportItemResponse toReportItem(LocalRadniNalog n,
                                                   Map<Long, LocalNarucitelj> naruciteljiMap,
                                                   Map<Long, LocalUser> usersMap) {
        List<Long> aktivnosti = parseAktivnostiJson(n.getAktivnosti());

        String assignedName = null;
        if (n.getAssignedUserId() != null) {
            LocalUser user = usersMap.get(n.getAssignedUserId());
            if (user != null) {
                String name = ((user.getIme() != null ? user.getIme() : "") + " " +
                        (user.getPrezime() != null ? user.getPrezime() : "")).trim();
                assignedName = name.isEmpty() ? user.getUsername() : name;
            }
        }

        String naruciteljNaziv = "-";
        if (n.getNaruciteljId() != null) {
            LocalNarucitelj nar = naruciteljiMap.get(n.getNaruciteljId());
            if (nar != null) naruciteljNaziv = nar.getName();
        }

        return NaloziReportItemResponse.builder()
                .id(n.getId())
                .datum(n.getDatum() != null ? n.getDatum().format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .status(n.deriveStatus())
                .naruciteljNaziv(naruciteljNaziv)
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
