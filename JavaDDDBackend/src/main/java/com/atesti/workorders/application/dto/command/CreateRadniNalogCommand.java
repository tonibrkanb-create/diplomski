package com.atesti.workorders.application.dto.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateRadniNalogCommand {
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
}
