package com.atesti.controller;

import com.atesti.dto.*;
import com.atesti.entity.Korisnik;
import com.atesti.entity.Obavijest;
import com.atesti.entity.Ponuda;
import com.atesti.entity.Recenzija;
import com.atesti.entity.SustavLog;
import com.atesti.security.JwtTokenProvider;
import com.atesti.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/management")
@RequiredArgsConstructor
public class ManagementController {

    private final PonudeManagementService ponudeManagementService;
    private final RecenzijeManagementService recenzijeManagementService;
    private final ObavijestiManagementService obavijestiManagementService;
    private final StatisticsService statisticsService;
    private final LogService logService;
    private final ReportsService reportsService;
    private final JwtTokenProvider jwtTokenProvider;

    // Ponude
    @GetMapping("/ponude")
    public ResponseEntity<List<Ponuda>> getAllPonude() {
        return ResponseEntity.ok(ponudeManagementService.getAll());
    }

    @GetMapping("/ponude/{id}")
    public ResponseEntity<Ponuda> getPonuda(@PathVariable Long id) {
        return ResponseEntity.ok(ponudeManagementService.getById(id));
    }

    @PutMapping("/ponude/{id}/status")
    public ResponseEntity<Ponuda> updatePonudaStatus(@PathVariable Long id, @RequestBody UpdatePonudaStatusRequest request) {
        return ResponseEntity.ok(ponudeManagementService.updateStatus(id, request));
    }

    // Recenzije
    @GetMapping("/recenzije")
    public ResponseEntity<List<Recenzija>> getAllRecenzije() {
        return ResponseEntity.ok(recenzijeManagementService.getAll());
    }

    @PutMapping("/recenzije/{id}/odgovor")
    public ResponseEntity<Recenzija> respondRecenzija(@PathVariable Long id, @RequestBody RespondRecenzijaRequest request) {
        return ResponseEntity.ok(recenzijeManagementService.respond(id, request));
    }

    // Obavijesti
    @PostMapping("/obavijesti")
    public ResponseEntity<Obavijest> sendObavijest(@RequestBody SendObavijestRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(obavijestiManagementService.create(request));
    }

    @GetMapping("/korisnici")
    public ResponseEntity<List<Korisnik>> getKorisnici() {
        return ResponseEntity.ok(obavijestiManagementService.getAllKorisnici());
    }

    // Statistics
    @GetMapping("/statistics/dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(statisticsService.getDashboardStats());
    }

    @GetMapping("/statistics/revenue")
    public ResponseEntity<List<RevenueItemResponse>> getRevenue() {
        return ResponseEntity.ok(statisticsService.getRevenueByAktivnost());
    }

    @GetMapping("/statistics/performance")
    public ResponseEntity<List<PerformanceItemResponse>> getPerformance() {
        return ResponseEntity.ok(statisticsService.getPerformanceByWorker());
    }

    @GetMapping("/statistics/monthly")
    public ResponseEntity<List<MonthlyItemResponse>> getMonthly() {
        return ResponseEntity.ok(statisticsService.getIssuedByMonth());
    }

    // Logs
    @GetMapping("/logs")
    public ResponseEntity<List<SustavLog>> getLogs(
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(logService.getAll(entity, action, from, to));
    }

    // Reports
    @GetMapping("/reports/nalozi")
    public ResponseEntity<List<NaloziReportItemResponse>> getNaloziReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(reportsService.getNaloziReport(from, to, status));
    }

    @GetMapping("/reports/narucitelji")
    public ResponseEntity<List<NaruciteljiReportItemResponse>> getNaruciteljiReport() {
        return ResponseEntity.ok(reportsService.getNaruciteljiReport());
    }

    // My tasks
    @GetMapping("/my-tasks")
    public ResponseEntity<List<NaloziReportItemResponse>> getMyTasks(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        return ResponseEntity.ok(reportsService.getMyTasks(userId));
    }

    private Long extractUserIdFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            return jwtTokenProvider.getUserIdFromToken(token);
        }
        return null;
    }
}
