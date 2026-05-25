package com.atesti.dto;

import lombok.Data;

@Data
public class KorisnikUpdateProfileRequest {
    private String ime;
    private String prezime;
    private String telefon;
    private String tvrtka;
    private String adresa;
    private String mjesto;
    private String postanskiBroj;
    private String drzava;
}
