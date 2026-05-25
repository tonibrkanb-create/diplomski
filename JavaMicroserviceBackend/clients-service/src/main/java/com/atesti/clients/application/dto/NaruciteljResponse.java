package com.atesti.clients.application.dto;

import com.atesti.clients.domain.model.Narucitelj;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaruciteljResponse {
    private Long id;
    private String name;
    private String adresa;
    private String mjesto;
    private String postanskiBroj;
    private String drzava;
    private String OIB;
    private String ziroRacun;
    private String ostalo;
    private String kontaktOsoba;
    private String telefon;
    private String mobitel;
    private String fax;
    private String email;
    private String location;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NaruciteljResponse from(Narucitelj entity) {
        return NaruciteljResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .adresa(entity.getAdresa())
                .mjesto(entity.getMjesto())
                .postanskiBroj(entity.getPostanskiBroj())
                .drzava(entity.getDrzava())
                .OIB(entity.getOIB())
                .ziroRacun(entity.getZiroRacun())
                .ostalo(entity.getOstalo())
                .kontaktOsoba(entity.getKontaktOsoba())
                .telefon(entity.getTelefon())
                .mobitel(entity.getMobitel())
                .fax(entity.getFax())
                .email(entity.getEmail())
                .location(entity.getLocation())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
