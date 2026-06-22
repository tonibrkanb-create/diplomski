package com.atesti.workorders.application.dto.query;

import com.atesti.workorders.domain.model.RadniNalogProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RadniNalogDetailResponse {

    private Long id;
    private String brojNaloga;
    private LocalDateTime datum;
    private String objekt;
    private Boolean fakturirano;
    private Boolean zavrseno;
    private String opis;
    private String brojPonude;
    private String brojRacuna;
    private String narudzbenica;
    private String ugovor;
    private String aktivnosti;
    private Long naruciteljId;
    private Long assignedUserId;

    public static RadniNalogDetailResponse from(RadniNalogProjection entity) {
        return RadniNalogDetailResponse.builder()
                .id(entity.getId())
                .brojNaloga(entity.getBrojNaloga())
                .datum(entity.getDatum())
                .objekt(entity.getObjekt())
                .fakturirano(entity.getFakturirano())
                .zavrseno(entity.getZavrseno())
                .opis(entity.getOpis())
                .brojPonude(entity.getBrojPonude())
                .brojRacuna(entity.getBrojRacuna())
                .narudzbenica(entity.getNarudzbenica())
                .ugovor(entity.getUgovor())
                .aktivnosti(entity.getAktivnosti())
                .naruciteljId(entity.getNaruciteljId())
                .assignedUserId(entity.getAssignedUserId())
                .build();
    }
}