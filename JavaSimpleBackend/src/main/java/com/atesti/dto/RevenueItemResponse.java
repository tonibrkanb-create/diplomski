package com.atesti.dto;

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
