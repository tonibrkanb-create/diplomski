package com.atesti.workorders.application.dto.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CreateRadniNalogCommand {
    private String brojNaloga;
    private Long naruciteljId;

    @JsonProperty("narucitelj_id")
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
}
