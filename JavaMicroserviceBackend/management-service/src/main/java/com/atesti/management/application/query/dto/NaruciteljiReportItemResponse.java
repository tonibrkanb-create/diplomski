package com.atesti.management.application.query.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaruciteljiReportItemResponse {
    private Long id;
    private String naziv;
    private String adresa;
    private String kontakt;
    private long naloziCount;
}
