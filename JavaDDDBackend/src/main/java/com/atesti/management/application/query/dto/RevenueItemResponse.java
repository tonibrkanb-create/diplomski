package com.atesti.management.application.query.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueItemResponse {
    private Long id;
    private String aktivnost;
    private double cijena;
    private long count;
    private double ukupno;
}
