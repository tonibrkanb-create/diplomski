package com.atesti.management.application.query.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceItemResponse {
    private Long id;
    private String username;
    private String ime;
    private String prezime;
    private long total;
    private long zavrseno;
    private long fakturirano;
}
