package com.atesti.workorders.infrastructure.web.model;

import com.atesti.workorders.application.dto.command.UpdateRadniNalogCommand;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRadniNalogRequest {

    private String brojNaloga;
    private Long naruciteljId;
    private Long naruciteljIdAlt;
    private String datum;
    private String objekt;
    private Boolean fakturirano;
    private Boolean zavrseno;
    private String opis;
    private String brojPonude;
    private String brojRacuna;
    private String narudzbenica;
    private String ugovor;
    private List<Object> aktivnosti;
    private String pdfUrl;
    private Long assignedUserId;

    public UpdateRadniNalogCommand toCommand() {
        return new UpdateRadniNalogCommand(
                brojNaloga,
                naruciteljId,
                naruciteljIdAlt,
                datum,
                objekt,
                fakturirano,
                zavrseno,
                opis,
                brojPonude,
                brojRacuna,
                narudzbenica,
                ugovor,
                aktivnosti,
                pdfUrl,
                assignedUserId
        );
    }
}