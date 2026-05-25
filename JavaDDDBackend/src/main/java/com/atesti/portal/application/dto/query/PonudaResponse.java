package com.atesti.portal.application.dto.query;

import com.atesti.portal.domain.model.Ponuda;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PonudaResponse {
    private Long id;
    private KorisnikProfileResponse korisnik;
    private Long korisnikId;
    private String opis;
    private String vrstaAtesta;
    private String lokacija;
    private LocalDate zeljeniDatum;
    private String status;
    private String odgovor;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PonudaResponse from(Ponuda entity) {
        return PonudaResponse.builder()
                .id(entity.getId())
                .korisnik(entity.getKorisnik() != null ? KorisnikProfileResponse.from(entity.getKorisnik()) : null)
                .korisnikId(entity.getKorisnikId())
                .opis(entity.getOpis())
                .vrstaAtesta(entity.getVrstaAtesta())
                .lokacija(entity.getLokacija())
                .zeljeniDatum(entity.getZeljeniDatum())
                .status(entity.getStatus())
                .odgovor(entity.getOdgovor())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
