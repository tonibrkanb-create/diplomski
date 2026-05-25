package com.atesti.portal.application.dto;

import lombok.Data;

@Data
public class CreatePonudaCommand {
    private String opis;
    private String vrstaAtesta;
    private String lokacija;
    private String zeljeniDatum;
}
