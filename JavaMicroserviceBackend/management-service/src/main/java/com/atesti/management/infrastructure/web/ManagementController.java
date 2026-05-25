package com.atesti.management.infrastructure.web;

import com.atesti.management.application.query.AuditLogQueryService;
import com.atesti.management.application.query.ReportsQueryService;
import com.atesti.management.application.query.StatisticsQueryService;
import com.atesti.management.application.query.dto.*;
import com.atesti.management.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/management")
@RequiredArgsConstructor
public class ManagementController {

    private final StatisticsQueryService statisticsQueryService;
    private final ReportsQueryService reportsQueryService;
    private final AuditLogQueryService auditLogQueryService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/statistics/dashboard")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(statisticsQueryService.getDashboardStats());
    }

    @GetMapping("/statistics/revenue")
    public ResponseEntity<List<RevenueItemResponse>> getRevenue() {
        return ResponseEntity.ok(statisticsQueryService.getRevenueByAktivnost());
    }

    @GetMapping("/statistics/performance")
    public ResponseEntity<List<PerformanceItemResponse>> getPerformance() {
        return ResponseEntity.ok(statisticsQueryService.getPerformanceByWorker());
    }

    @GetMapping("/statistics/monthly")
    public ResponseEntity<List<MonthlyItemResponse>> getMonthly() {
        return ResponseEntity.ok(statisticsQueryService.getIssuedByMonth());
    }

    @GetMapping("/logs")
    public ResponseEntity<List<SustavLogResponse>> getLogs(
            @RequestParam(required = false) String entity,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(auditLogQueryService.getFiltered(entity, action, from, to));
    }

    @GetMapping("/reports/nalozi")
    public ResponseEntity<List<NaloziReportItemResponse>> getNaloziReport(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(reportsQueryService.getNaloziReport(from, to, status));
    }

    @GetMapping("/reports/narucitelji")
    public ResponseEntity<List<NaruciteljiReportItemResponse>> getNaruciteljiReport() {
        return ResponseEntity.ok(reportsQueryService.getNaruciteljiReport());
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<NaloziReportItemResponse>> getMyTasks(HttpServletRequest request) {
        Long userId = extractUserIdFromRequest(request);
        return ResponseEntity.ok(reportsQueryService.getMyTasks(userId));
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
