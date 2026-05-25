package com.atesti.management.application.query.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalNalozi;
    private long totalNarucitelji;
    private long fakturirano;
    private long zavrseno;
    private long nefakturirano;
    private long uTijeku;
}
