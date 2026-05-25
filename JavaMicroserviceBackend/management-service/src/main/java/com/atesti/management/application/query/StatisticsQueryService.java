package com.atesti.management.application.query;

import com.atesti.management.application.query.dto.*;
import com.atesti.management.projection.model.LocalAktivnost;
import com.atesti.management.projection.model.LocalRadniNalog;
import com.atesti.management.projection.model.LocalUser;
import com.atesti.management.projection.repository.LocalAktivnostRepository;
import com.atesti.management.projection.repository.LocalNaruciteljRepository;
import com.atesti.management.projection.repository.LocalRadniNalogRepository;
import com.atesti.management.projection.repository.LocalUserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsQueryService {

    private final LocalRadniNalogRepository radniNalogRepository;
    private final LocalNaruciteljRepository naruciteljRepository;
    private final LocalAktivnostRepository aktivnostRepository;
    private final LocalUserRepository userRepository;
    private final ObjectMapper objectMapper;

    public DashboardStatsResponse getDashboardStats() {
        long totalNalozi = radniNalogRepository.count();
        long totalNarucitelji = naruciteljRepository.count();
        long fakturirano = radniNalogRepository.countByFakturirano(true);
        long zavrseno = radniNalogRepository.countByZavrseno(true);

        return DashboardStatsResponse.builder()
                .totalNalozi(totalNalozi)
                .totalNarucitelji(totalNarucitelji)
                .fakturirano(fakturirano)
                .zavrseno(zavrseno)
                .nefakturirano(totalNalozi - fakturirano)
                .uTijeku(totalNalozi - zavrseno)
                .build();
    }

    public List<RevenueItemResponse> getRevenueByAktivnost() {
        List<LocalAktivnost> aktivnosti = aktivnostRepository.findByIsActiveTrueOrderByIdAsc();
        List<LocalRadniNalog> nalozi = radniNalogRepository.findAll();

        Map<Long, Long> countMap = new HashMap<>();
        for (LocalRadniNalog nalog : nalozi) {
            List<Long> ids = parseAktivnostiJson(nalog.getAktivnosti());
            for (Long id : ids) {
                countMap.merge(id, 1L, Long::sum);
            }
        }

        return aktivnosti.stream().map(a -> {
            long count = countMap.getOrDefault(a.getId(), 0L);
            double cijena = a.getCijena() != null ? a.getCijena().doubleValue() : 0;
            return RevenueItemResponse.builder()
                    .id(a.getId())
                    .aktivnost(a.getAktivnost())
                    .cijena(cijena)
                    .count(count)
                    .ukupno(cijena * count)
                    .build();
        }).collect(Collectors.toList());
    }

    public List<PerformanceItemResponse> getPerformanceByWorker() {
        List<LocalUser> users = userRepository.findAllByOrderByIdAsc();
        List<LocalRadniNalog> assigned = radniNalogRepository.findByAssignedUserIdIsNotNull();

        Map<Long, List<LocalRadniNalog>> byUser = assigned.stream()
                .collect(Collectors.groupingBy(LocalRadniNalog::getAssignedUserId));

        return users.stream().map(u -> {
            List<LocalRadniNalog> userNalozi = byUser.getOrDefault(u.getId(), List.of());
            return PerformanceItemResponse.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .ime(u.getIme())
                    .prezime(u.getPrezime())
                    .total(userNalozi.size())
                    .zavrseno(userNalozi.stream().filter(n -> Boolean.TRUE.equals(n.getZavrseno())).count())
                    .fakturirano(userNalozi.stream().filter(n -> Boolean.TRUE.equals(n.getFakturirano())).count())
                    .build();
        }).collect(Collectors.toList());
    }

    public List<MonthlyItemResponse> getIssuedByMonth() {
        List<LocalRadniNalog> nalozi = radniNalogRepository.findAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");

        Map<String, Long> grouped = nalozi.stream()
                .filter(n -> n.getDatum() != null)
                .collect(Collectors.groupingBy(
                        n -> n.getDatum().format(fmt),
                        TreeMap::new,
                        Collectors.counting()));

        return grouped.entrySet().stream()
                .map(e -> MonthlyItemResponse.builder()
                        .month(e.getKey())
                        .count(e.getValue())
                        .build())
                .collect(Collectors.toList());
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
