package com.atesti.portal.application.dto.query;

import com.atesti.portal.domain.model.Obavijest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObavijestResponse {
    private Long id;
    private KorisnikProfileResponse korisnik;
    private Long korisnikId;
    private String naslov;
    private String poruka;
    private Boolean procitana;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ObavijestResponse from(Obavijest entity) {
        return ObavijestResponse.builder()
                .id(entity.getId())
                .korisnik(entity.getKorisnik() != null ? KorisnikProfileResponse.from(entity.getKorisnik()) : null)
                .korisnikId(entity.getKorisnikId())
                .naslov(entity.getNaslov())
                .poruka(entity.getPoruka())
                .procitana(entity.getProcitana())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
