package com.atesti.dto;

import lombok.Data;

@Data
public class PonudaRequest {
    private String opis;
    private String vrstaAtesta;
    private String lokacija;
    private String zeljeniDatum;
}
