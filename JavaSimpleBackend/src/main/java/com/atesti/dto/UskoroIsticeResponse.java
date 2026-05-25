package com.atesti.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UskoroIsticeResponse {
    private Long id;
    private String narucitelj;
    private String radniNalog;
    private String aktivnost;
    private LocalDate datumIsteka;
    private Boolean isActive;
}
