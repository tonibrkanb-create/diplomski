package com.atesti.portal.application.dto.query;

import com.atesti.portal.domain.model.Korisnik;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KorisnikProfileResponse {
    private Long id;
    private String ime;
    private String prezime;
    private String email;
    private String telefon;
    private String tvrtka;
    private String adresa;
    private String mjesto;
    private String postanskiBroj;
    private String drzava;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static KorisnikProfileResponse from(Korisnik entity) {
        return KorisnikProfileResponse.builder()
                .id(entity.getId())
                .ime(entity.getIme())
                .prezime(entity.getPrezime())
                .email(entity.getEmail())
                .telefon(entity.getTelefon())
                .tvrtka(entity.getTvrtka())
                .adresa(entity.getAdresa())
                .mjesto(entity.getMjesto())
                .postanskiBroj(entity.getPostanskiBroj())
                .drzava(entity.getDrzava())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
