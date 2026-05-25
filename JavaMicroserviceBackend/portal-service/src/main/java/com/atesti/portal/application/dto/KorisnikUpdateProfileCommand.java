package com.atesti.portal.application.dto;

import lombok.Data;

@Data
public class KorisnikUpdateProfileCommand {
    private String ime;
    private String prezime;
    private String telefon;
    private String tvrtka;
    private String adresa;
    private String mjesto;
    private String postanskiBroj;
    private String drzava;
}
