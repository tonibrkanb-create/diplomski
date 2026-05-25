package com.atesti.service;

import com.atesti.dto.*;
import com.atesti.entity.Aktivnost;
import com.atesti.entity.RadniNalog;
import com.atesti.entity.User;
import com.atesti.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final RadniNalogRepository radniNalogRepository;
    private final NaruciteljRepository naruciteljRepository;
    private final AktivnostRepository aktivnostRepository;
    private final UserRepository userRepository;
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
        List<Aktivnost> aktivnosti = aktivnostRepository.findByIsActiveTrueOrderByIdAsc();
        List<RadniNalog> nalozi = radniNalogRepository.findAll();

        Map<Long, Long> countMap = new HashMap<>();
        for (RadniNalog nalog : nalozi) {
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
        List<User> users = userRepository.findAllByOrderByIdAsc();
        List<RadniNalog> assigned = radniNalogRepository.findByAssignedUserIdIsNotNull();

        Map<Long, List<RadniNalog>> byUser = assigned.stream()
                .collect(Collectors.groupingBy(n -> n.getAssignedUser().getId()));

        return users.stream().map(u -> {
            List<RadniNalog> userNalozi = byUser.getOrDefault(u.getId(), List.of());
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
        List<RadniNalog> nalozi = radniNalogRepository.findAll();
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
