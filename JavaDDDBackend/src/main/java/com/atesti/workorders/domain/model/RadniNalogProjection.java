package com.atesti.workorders.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RadniNalogProjection {

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

    public RadniNalogProjection(
            Long id,
            String brojNaloga,
            Long naruciteljId,
            Boolean fakturirano,
            Boolean zavrseno
    ) {
        this.id = id;
        this.brojNaloga = brojNaloga;
        this.naruciteljId = naruciteljId;
        this.fakturirano = fakturirano;
        this.zavrseno = zavrseno;
    }

    public RadniNalogProjection(
            Long id,
            String brojNaloga,
            LocalDateTime datum,
            String objekt,
            Boolean fakturirano,
            Boolean zavrseno,
            String opis,
            String brojPonude,
            String brojRacuna,
            String narudzbenica,
            String ugovor,
            String aktivnosti,
            Long naruciteljId,
            Long assignedUserId
    ) {
        this.id = id;
        this.brojNaloga = brojNaloga;
        this.datum = datum;
        this.objekt = objekt;
        this.fakturirano = fakturirano;
        this.zavrseno = zavrseno;
        this.opis = opis;
        this.brojPonude = brojPonude;
        this.brojRacuna = brojRacuna;
        this.narudzbenica = narudzbenica;
        this.ugovor = ugovor;
        this.aktivnosti = aktivnosti;
        this.naruciteljId = naruciteljId;
        this.assignedUserId = assignedUserId;
    }
}