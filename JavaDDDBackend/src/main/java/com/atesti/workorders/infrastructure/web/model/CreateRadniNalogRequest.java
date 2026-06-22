package com.atesti.workorders.infrastructure.web.model;

import com.atesti.workorders.application.dto.command.CreateRadniNalogCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateRadniNalogRequest {

    private String brojNaloga;

    @NotNull
    private Long naruciteljId;

    private String datum;
    private String objekt;
    private Boolean fakturirano;
    private Boolean zavrseno;
    private String opis;
    private String brojPonude;
    private String brojRacuna;
    private String narudzbenica;
    private String ugovor;

    @NotEmpty
    private List<Object> aktivnosti;

    private String pdfUrl;
    private Long assignedUserId;

    public CreateRadniNalogCommand toCommand() {
        return new CreateRadniNalogCommand(
                brojNaloga,
                naruciteljId,
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