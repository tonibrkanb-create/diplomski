package com.atesti.portal.application.dto;

import com.atesti.portal.domain.model.Recenzija;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecenzijaResponse {
    private Long id;
    private KorisnikProfileResponse korisnik;
    private Long korisnikId;
    private Long radniNalogId;
    private Integer ocjena;
    private String komentar;
    private String odgovor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RecenzijaResponse from(Recenzija entity) {
        return RecenzijaResponse.builder()
                .id(entity.getId())
                .korisnik(entity.getKorisnik() != null ? KorisnikProfileResponse.from(entity.getKorisnik()) : null)
                .korisnikId(entity.getKorisnikId())
                .radniNalogId(entity.getRadniNalogId())
                .ocjena(entity.getOcjena())
                .komentar(entity.getKomentar())
                .odgovor(entity.getOdgovor())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
