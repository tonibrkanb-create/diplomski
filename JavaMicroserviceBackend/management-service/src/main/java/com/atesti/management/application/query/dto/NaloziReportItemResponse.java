package com.atesti.management.application.query.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaloziReportItemResponse {
    private Long id;
    private String datum;
    private String status;
    private String naruciteljNaziv;
    private String assignedUser;
    private int aktivnostiCount;
}
